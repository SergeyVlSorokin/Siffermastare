# Technical Specification: Bayesian Knowledge Tracing Engine

## 1. Overview
This document defines the **Adaptive Learning Engine** for Siffermästare. The system uses a **Bayesian approach** (Beta Distributions) to model the user's mastery of specific number "atoms". This allows the system to:
1.  Track proficiency as a probability distribution rather than a simple percentage.
2.  Adapt to user improvement over time (non-stationarity) using a decay factor.
3.  Decompose complex numbers into constituent parts to pinpoint specific weaknesses.

## 2. The Atomic Model
Swedish numbers are constructed from fundamental building blocks. We track mastery for each block ("Atom") independently.

### 2.1 Atomic Units
The system tracks probability distributions for the following **30 Atoms**:
1.  **Digits (0-9):** `0, 1, 2, ..., 9`
    *   *Note:* `0` is rarely used in compound numbers but is a valid target itself.
2.  **Teens (10-19):** `10, 11, 12, ..., 19`
    *   *Reason:* These are irregular in Swedish (e.g., *elva*, *tolv*, *tretton*) and cannot be reliably constructed from digits.
3.  **Tens (20-90):** `20, 30, 40, ..., 90`
    *   *Reason:* These are distinct stems (e.g., *tjugo*, *trettio*).

### 2.2 Composite Numbers
Any number outside these atoms is treated as a **Composite**.
*   **Example: "25"**
    *   Decomposed into: `{20, 5}`
*   **Example: "123"**
    *   Decomposed into: `{1, 20, 3}`

## 3. Statistical Model
We model the probability $\theta$ that a user knows a specific Atom using a **Beta Distribution**:
$$ P(\theta) \sim Beta(\alpha, \beta) $$

*   $\alpha$ (alpha): Represents accumulated "success" weight.
*   $\beta$ (beta): Represents accumulated "failure" weight.
*   **Mean Probability:** $\mu = \frac{\alpha}{\alpha + \beta}$ (Expected likelihood of correct answer)
*   **Uncertainty:** Variance decreases as $\alpha + \beta$ increases.

### 3.1 Handling Non-Stationarity (Learning)
Since users *learn* (i.e., true probability $\theta$ increases over time), the distribution is **non-stationary**. We cannot simple sum all history forever, or old mistakes would permanently drag down the score.

We use a **Forgetting Factor (Decay) $\lambda$**:
*   Suggested $\lambda = 0.9$ (Configurable parameter)

**Update Rule:**
When an event occurs for a specific Atom at time $t$, we apply the decay $\lambda$ and update based on the outcome.

**Case 1: Correct Answer (Success)**
We use the **Confidence Weight ($W$)** calculated from speed (see 3.3).
$$ \alpha_t = \lambda \cdot \alpha_{t-1} + W $$
$$ \beta_t = \lambda \cdot \beta_{t-1} $$

**Case 2: Incorrect Answer (Failure)**
We always use a standard weight of 1.0 for failures (speed does not mitigate a mistake).
$$ \alpha_t = \lambda \cdot \alpha_{t-1} $$
$$ \beta_t = \lambda \cdot \beta_{t-1} + 1.0 $$

*Effect:* The "effective sample size" tends towards $\frac{1}{1-\lambda}$. For $\lambda=0.9$, the system "remembers" roughly the last 10 interactions. This allows the model to "forget" early struggles as recent performance improves.

### 3.2 Initialization
All Atoms start with a **Flat Prior**:
*   $\alpha_0 = 1.0$
*   $\beta_0 = 1.0$

### 3.3 Time-Weighted Updates (Fluency)
We distinguish between "Computation" (slow, hesitant) and "Fluency" (fast, automated) by tracking the **Mean Time Per Event (MPE)**.

**Metric:**
$$ \text{MPE} = \frac{\text{Time(TTS Finish) to Time(Submit)}}{\text{Character Count of Standard Answer}} $$

**Confidence Weight Function:**
We use a **Clamped Inverse** model to scale the update weight ($W$). Fast answers count *more* towards mastery; slow answers count *less*.

$$ W = \text{clamp}\left( \frac{800ms}{\text{MPE}}, \ 0.2, \ 1.3 \right) $$

**Fluency Tiers:**
*   **Fast (< 615ms/char):** $W = 1.3$ (Bonus! High confidence)
*   **Standard (800ms/char):** $W = 1.0$ (Normal update)
*   **Slow (> 4000ms/char):** $W = 0.2$ (Minimal progress)

*Rationale:* A user who answers "7" in 5 seconds ($MPE=5000$) is likely counting on fingers. They calculated it correctly, but they have not *mastered* it. The low weight (0.2) means their $\alpha$ barely moves, keeping their variance high and ensuring the system tests them again soon.

## 4. Assessment Logic

### 4.1 Decomposition & Grading
When a user answers a question, we decompose the *Target Number* into its Atoms and grade them based on the *User's Input*.

**Scenario A: Correct Answer**
*   **Target:** 25 (Atoms: `20`, `5`)
*   **Input:** 25
*   **Result:**
    *   Atom `20`: Update with **Success** (1)
    *   Atom `5`: Update with **Success** (1)

**Scenario B: Partial Error (Correct Structure)**
*   **Target:** 25 (Atoms: `20`, `5`)
*   **Input:** 24 (Decomposed: `20`, `4`)
*   **Logic:** The user correctly identified the "Tens" part but missed the "Digit" part.
*   **Result:**
    *   Atom `20`: Update with **Success** (1)
    *   Atom `5`: Update with **Failure** (0)
    *   *Note:* The extra input computation (`4`) is ignored for grading the *Target's* atoms. We strictly track: "Did the user recognize the atoms present in the stimulus?"

**Scenario C: Full Error**
*   **Target:** 25 (Atoms: `20`, `5`)
*   **Input:** 99 (Decomposed: `90`, `9`)
*   **Logic:** The answer does not align structurally or is completely wrong. We cannot assume partial credit.
*   **Result:**
    *   Atom `20`: Update with **Failure** (0)
    *   Atom `5`: Update with **Failure** (0)
    *   *Rationale:* The user failed to identify *any* part of the stimulus correctly.

**Scenario D: Structural Error**
*   **Target:** 25 (Atoms: `20`, `5`)
*   **Input:** 732 (Mismatched length)
*   **Logic:** The answer does not align structurally.
*   **Result:**
    *   Atom `20`: Update with **Failure** (0)
    *   Atom `5`: Update with **Failure** (0)
    *   *Rationale:* The user failed to identify *any* part of the stimulus correctly.


## 5. Usage & Storage

### 5.1 Database Schema (Proposed)
A new Room entity `AtomState` to persist the distribution parameters.

```kotlin
@Entity(tableName = "atom_states")
data class AtomState(
    @PrimaryKey val atomId: String, // e.g., "5", "20"
    val alpha: Float,
    val beta: Float,
    val lastUpdated: Long
)
```

### 5.2 Algorithm Summary (Pseudocode)

The grading logic is delegated to an `EvaluationStrategy` provided by the Generator. This allows different lessons to handle ambiguity (e.g., "14:00" vs "2:00") and decomposition rules differently. Code here is just an example to demonstate idea of an interface and should not be treated as valid solution for Informal time lesson, the real one should have better algorithm.

```kotlin
interface EvaluationStrategy {
    fun evaluate(userInput: String, question: Question): EvaluationResult
}

// Example: Handling Informal Time Ambiguity
data class EvaluationResult(
    val isCorrect: Boolean,
    val atomUpdates: Map<String, List<Boolean>> // AtomID -> List of [Success, Failure...]
)

class InformalTimeStrategy : EvaluationStrategy {
    override fun evaluate(userInput: String, question: Question): EvaluationResult {
        // ... (normalization)
        
        val targetAtoms = question.atoms // e.g., ["5", "5"] for "55"
        val inputAtoms = decompose(normalizedInput) // e.g., ["5"]
        
        // Grade using Bag Logic (Multi-set difference)
        val updates = mutableMapOf<String, MutableList<Boolean>>()
        
        // Count frequencies in Target
        val targetCounts = targetAtoms.groupBy { it }.mapValues { it.value.size }
        // Count frequencies in Input
        val inputCounts = inputAtoms.groupBy { it }.mapValues { it.value.size }
        
        targetCounts.forEach { (atomId, requiredCount) ->
            val providedCount = inputCounts[atomId] ?: 0
            val matches = minOf(requiredCount, providedCount)
            val misses = requiredCount - matches
            
            val atomResults = ArrayList<Boolean>()
            repeat(matches) { atomResults.add(true) }
            repeat(misses) { atomResults.add(false) }
            
            updates[atomId] = atomResults
        }
        
        return EvaluationResult(
            isCorrect = updates.values.flatten().all { it },
            atomUpdates = updates
        )
    }
}
```

## 6. Future Implementation: Active Selection
While not part of this specific requirement, this model enables **Thompson Sampling** or **Uncertainty Sampling** for selecting the next question.
*   *Idea:* Sample a probability $p \sim Beta(\alpha, \beta)$ for all atoms. Pick atoms with low $p$ (to drill weaknesses) or atoms with high variance (to explore uncertainty).
