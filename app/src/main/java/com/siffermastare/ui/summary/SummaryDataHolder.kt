package com.siffermastare.ui.summary

import com.siffermastare.domain.models.AtomSummary

/**
 * Shared in-memory holder for passing atom summary data across navigation
 * without encoding complex lists into nav args.
 *
 * Data is cleared after consumption to avoid stale reads.
 */
object SummaryDataHolder {
    private var improved: List<AtomSummary> = emptyList()
    private var needsPractice: List<AtomSummary> = emptyList()
    private var hasData: Boolean = false

    fun set(improved: List<AtomSummary>, needsPractice: List<AtomSummary>) {
        this.improved = improved
        this.needsPractice = needsPractice
        this.hasData = true
    }

    fun consume(): Pair<List<AtomSummary>, List<AtomSummary>>? {
        if (!hasData) return null
        val result = Pair(improved, needsPractice)
        improved = emptyList()
        needsPractice = emptyList()
        hasData = false
        return result
    }
}
