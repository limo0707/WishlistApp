package com.example.wishlistapp

import android.os.Bundle
import android.util.SparseBooleanArray
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wishlistapp.databinding.ActivityGroceryBinding

class GroceryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroceryBinding
    private val groceryList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroceryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_multiple_choice, // 체크박스
            groceryList
        )
        binding.listViewGrocery.adapter = adapter
        binding.listViewGrocery.choiceMode = ListView.CHOICE_MODE_MULTIPLE // 체크박스 다중 체크 허용

        // 추가하기
        binding.btnAdd.setOnClickListener {
            val item = binding.etGroceryItem.text.toString()
            if (item.isNotEmpty()) {
                groceryList.add(item)
                adapter.notifyDataSetChanged()
                binding.etGroceryItem.text.clear()
            } else {
                Toast.makeText(this, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. 체크된 항목 삭제하기
        binding.btnDeleteChecked.setOnClickListener {
            val checkedItems: SparseBooleanArray = binding.listViewGrocery.checkedItemPositions // 현재 체크된 아이템들의 인덱스 정보 가져옴
            var deletedCount = 0

            // [중요] 역순 반복문 (downTo)
            // 설명: 리스트의 끝에서부터 0번 인덱스 방향으로 반복합니다.
            // 의도: 앞에서부터 삭제하면 인덱스가 밀려서 엉뚱한 것이 삭제되거나 오류가 납니다. (트러블슈팅 참조)
            for (i in adapter.count - 1 downTo 0) {
                if (checkedItems.get(i)) { // 해당 인덱스가 체크되어 있다면
                    groceryList.removeAt(i) // 데이터 삭제
                    deletedCount++
                }
            }

            if (deletedCount > 0) {
                binding.listViewGrocery.clearChoices() // 체크 정보 초기화
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "$deletedCount 개 삭제 완료", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "선택된 항목이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        // 롱클릭 - 수정/삭제 메뉴 띄우기
        binding.listViewGrocery.setOnItemLongClickListener { parent, view, position, id ->
            val options = arrayOf("수정하기", "이 항목만 삭제하기")

            AlertDialog.Builder(this)
                .setTitle("작업 선택")
                .setItems(options) { dialog, which ->
                    when (which) {
                        0 -> showEditDialog(position) // 수정하기
                        1 -> deleteItem(position) // 삭제하기
                    }
                }
                .show()

            return@setOnItemLongClickListener true // true 반환으로 롱클릭 이벤트 완료
        }
    }

    // 수정하기
    private fun showEditDialog(position: Int) {
        val currentItem = groceryList[position]
        val editText = EditText(this)
        editText.setText(currentItem)
        editText.setSelection(editText.text.length) // 커서를 글자 끝으로 이동

        AlertDialog.Builder(this)
            .setTitle("수정하기")
            .setView(editText)
            .setPositiveButton("저장") { dialog, _ ->
                val newText = editText.text.toString()
                if (newText.isNotEmpty()) {
                    groceryList[position] = newText // 리스트의 해당 위치 값 업데이트
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 선택된 항목만 삭제하기
    private fun deleteItem(position: Int) {
        groceryList.removeAt(position)

        binding.listViewGrocery.clearChoices()
        adapter.notifyDataSetChanged()

        Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
    }
}