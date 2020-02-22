package app.sano.picchi.todoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var realm: Realm
    lateinit var listView: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //realmを開く
        //これないと落ちる
        Realm.init(this)
        realm = Realm.getDefaultInstance()




        listView = findViewById(R.id.listView) as ListView




        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val memo = parent.getItemAtPosition(position) as Memo
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra("updateDate", memo.updateDate)
                startActivity(intent)
            }
    }



    fun setMemoList() {
        //realmから読み取る
        val results = realm.where(Memo::class.java).findAll()
        val items = realm.copyFromRealm(results)

        val adapter = MemoAdapter(this, R.layout.layout_item_memo, items)

        listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        setMemoList()
    }


    fun create(view: View) {
        val intent = Intent(this, AddActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        //一応いれとく
        //super.onDestroy()
        //realmを閉じる
        realm.close()
    }







    //メニューのとこ
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater

        inflater.inflate(R.menu.option_menu_list, menu)

        return super.onCreateOptionsMenu(menu)

    }

    //メニューのとこ
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.getItemId()
        var displayedStr: String? = null // 画面に表示する文字列

        when (itemId) {
            R.id.option_menu_1 -> displayedStr = "All ToDo"
            R.id.option_menu_2 -> displayedStr = "Active ToDo"
            R.id.option_menu_3 -> displayedStr = "Done ToDo"
        }

//        javaの書き方で型変換のエラー出るから下に変更
//        val tv = findViewById(R.id.mainStr)
//        tv.setText(displayedStr)
        mainStr.text = displayedStr

        return super.onOptionsItemSelected(item)

    }
}
