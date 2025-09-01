package com.example.loginsignupapp

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupapp.databinding.ActivityDashboardBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPieChart()
        setupBarChart()
    }

    private fun setupPieChart() {
        val pieChart = binding.pieChart

        val entries = listOf(
            PieEntry(6500f, "Income"),
            PieEntry(4200f, "Expenses")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(Color.BLUE, Color.GREEN)
        dataSet.valueTextSize = 16f

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.centerText = "₹10,700"
        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun setupBarChart() {
        val barChart = binding.barChart

        val entries = listOf(
            BarEntry(0f, 3000f), // Jun
            BarEntry(1f, 5000f), // Jul
            BarEntry(2f, 4200f)  // Aug
        )

        val dataSet = BarDataSet(entries, "Income & Expenses")
        dataSet.colors = listOf(Color.BLUE, Color.GREEN)
        dataSet.valueTextSize = 14f

        val barData = BarData(dataSet)
        barChart.data = barData

        val months = listOf("Jun", "Jul", "Aug")
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(months)
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false

        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }
}