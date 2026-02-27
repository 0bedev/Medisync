package com.example.medisync

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CitaDecorator(
    private val dias: HashSet<CalendarDay>
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dias.contains(day)
    }

    override fun decorate(view: DayViewFacade) {

        view.addSpan(ForegroundColorSpan(Color.RED))

    }
}