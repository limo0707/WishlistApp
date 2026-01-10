package com.example.wishlistapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wishlistapp.databinding.ActivityTravelBinding

class TravelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTravelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTravelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalculate.setOnClickListener {
            val destStr = binding.etDest.text.toString()
            val goalStr = binding.etGoal.text.toString()
            val savedStr = binding.etSaved.text.toString()

            // 입력이 비었는지 확인
            if (destStr.isEmpty() || goalStr.isEmpty() || savedStr.isEmpty()) {
                Toast.makeText(this, "모든 칸을 입력해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 함수 강제 종료
            }

            // 문자열을 실수형으로 변환
            val goal = goalStr.toDouble()
            val saved = savedStr.toDouble()

            // 0으로 나누기 방지
            if (goal == 0.0) {
                Toast.makeText(this, "목표 금액은 0원보다 큰 값을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 계산 로직: (현재저축액 / 목표액) * 100
            val percent = (saved / goal) * 100
            val percentInt = percent.toInt() // 소수점은 버리고 정수로 표현

            binding.tvResultTitle.text = "$destStr 여행까지"

            // 프로그레스바 설정
            binding.progressBar.progress = percentInt
            binding.tvPercentage.text = "$percentInt% 달성!"
            if (percentInt >= 100) binding.progressBar.progress = 100// 최대치는 100으로 고정

            binding.layoutInput.visibility = View.GONE
            binding.layoutResult.visibility = View.VISIBLE
        }

        // 수정하기
        binding.btnEdit.setOnClickListener {
            binding.layoutResult.visibility = View.GONE
            binding.layoutInput.visibility = View.VISIBLE
        }

        // 삭제하기
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("계획 삭제")
                .setMessage("현재 여행 계획을 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    binding.etDest.text.clear()
                    binding.etGoal.text.clear()
                    binding.etSaved.text.clear()

                    binding.layoutResult.visibility = View.GONE
                    binding.layoutInput.visibility = View.VISIBLE

                    Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }
}