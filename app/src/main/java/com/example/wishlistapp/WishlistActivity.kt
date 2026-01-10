package com.example.wishlistapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wishlistapp.databinding.ActivityWishlistBinding

data class WishItem(var name: String, var price: String) {
    override fun toString(): String {
        return "$name  |  $price 원"
    }
}

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding
    private val dataList = ArrayList<WishItem>() // WishItem 객체들을 담을 리스트
    private lateinit var adapter: ArrayAdapter<WishItem> // 리스트와 화면 연결

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 설정
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 어댑터 초기화 및 연결
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        binding.listView.adapter = adapter

        // 추가하기
        binding.btnAdd.setOnClickListener {
            val name = binding.etItemName.text.toString()
            val price = binding.etItemPrice.text.toString()

            if (name.isNotEmpty() && price.isNotEmpty()) {
                val newItem = WishItem(name, price)
                dataList.add(newItem)

                adapter.notifyDataSetChanged()

                binding.etItemName.text.clear()
                binding.etItemPrice.text.clear()
            } else {
                Toast.makeText(this, "이름과 가격을 모두 입력해주세요.", Toast.LENGTH_SHORT).show() // 이름, 가격 둘 다 입력하지 않을 경우 안내 메시지
            }
        }

        // 수정하기 - 짧게 눌렀을 때
        binding.listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = dataList[position]
            val dialogView = layoutInflater.inflate(R.layout.dialog_wish_edit, null)
            val etName = dialogView.findViewById<EditText>(R.id.etEditName)
            val etPrice = dialogView.findViewById<EditText>(R.id.etEditPrice)

            // 기존 데이터
            etName.setText(selectedItem.name)
            etPrice.setText(selectedItem.price)

            AlertDialog.Builder(this)
                .setTitle("수정")
                .setView(dialogView) // 위에서 만든 커스텀 뷰를 다이얼로그 내용으로 설정
                .setPositiveButton("저장") { dialog, which ->
                    val newName = etName.text.toString()
                    val newPrice = etPrice.text.toString()

                    if (newName.isNotEmpty() && newPrice.isNotEmpty()) {
                        selectedItem.name = newName
                        selectedItem.price = newPrice

                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("취소", null) // 취소 시 아무 동작 안 함
                .show()
        }

        // 삭제하기 - 길게 눌렀을 때
        binding.listView.setOnItemLongClickListener { parent, view, position, id ->
            AlertDialog.Builder(this)
                .setTitle("삭제 확인")
                .setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("삭제") { dialog, which ->
                    dataList.removeAt(position) // 리스트에서 해당 위치 데이터 삭제
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("취소", null)
                .show()

            return@setOnItemLongClickListener true // true 반환으로 롱클릭 이벤트 완료
        }
    }
}