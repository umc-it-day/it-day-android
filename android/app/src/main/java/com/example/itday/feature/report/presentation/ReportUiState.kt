package com.example.itday.feature.report.presentation

data class ReportUiState(val access: ReportAccess = ReportAccess.Guest)

enum class ReportAccess { Guest }
