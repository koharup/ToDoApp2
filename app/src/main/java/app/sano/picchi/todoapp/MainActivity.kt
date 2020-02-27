package app.sano.picchi.todoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CustomOnItemClickLister {


    //あとで初期化を行うような変数をつくることができる
    lateinit var realm: Realm
    lateinit var listView: ListView
    lateinit var itemList: MutableList<Memo>
    lateinit var adapter: ArrayAdapter<Memo>
    lateinit var results:RealmResults<Memo>


    //overrideはピヨ太の継承
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //realmを開く
        //これないと落ちる
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        listView = findViewById(R.id.listView) as ListView
    }


    //listにrealmの情報を表示する
    fun setMemoList() {
        //realmから読み取る
        results = realm.where(Memo::class.java).findAll()
        itemList = realm.copyFromRealm(results)
        adapter = MemoAdapter(this, R.layout.layout_item_memo, itemList, this)
        listView.adapter = adapter
    }

    //戻ってきたときや表示されるときに呼ばれる
    override fun onResume() {
        super.onResume()
        //これがないとlistの中に何も表示されなくなる
        setMemoList()
    }


    //画面遷移
    fun create(view: View) {
        val intent = Intent(this, AddActivity::class.java)
        startActivity(intent)
    }

    //最後に呼ばれる
    override fun onDestroy() {
        super.onDestroy()

        //一応いれとく
        //super.onDestroy()
        //realmを閉じる
        realm.close()
    }


    //メニューの表示
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //menuInflater == getMenuInflater()というメソッドを使うことで、main.xmlの内容を読み込んで、メニューに反映させ
        val inflater = menuInflater

        // 参照するリソースは上でリソースファイルに付けた名前と同じもの
        inflater.inflate(R.menu.option_menu_list, menu)

        return super.onCreateOptionsMenu(menu)

    }

    //メニューのとこ
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.getItemId()
        var displayedStr: String? = null // 画面に表示する文字列

        when (itemId) {
            R.id.option_menu_1 -> {
                displayedStr = "All ToDo"
                //realmから読み取る
                val results = realm.where(Memo::class.java).findAll()
                itemList = realm.copyFromRealm(results)
                Log.e("TAG", itemList.size.toString())
                adapter = MemoAdapter(this, R.layout.layout_item_memo, itemList, this)
                listView.adapter = adapter
            }
            R.id.option_menu_2 -> {
                displayedStr = "Active ToDo"
                //realmから読み取る
                val results = realm.where(Memo::class.java).equalTo("ischeck", false).findAll();
                itemList = realm.copyFromRealm(results)
                Log.e("TAG", itemList.size.toString())
                adapter = MemoAdapter(this, R.layout.layout_item_memo, itemList, this)
                listView.adapter = adapter

            }
            R.id.option_menu_3 -> {
                displayedStr = "Done ToDo"
                val results = realm.where(Memo::class.java).equalTo("ischeck", true).findAll();
                itemList = realm.copyFromRealm(results)
                Log.e("TAG", itemList.size.toString())
                adapter = MemoAdapter(this, R.layout.layout_item_memo, itemList, this)
                listView.adapter = adapter

            }
        }

//        javaの書き方で型変換のエラー出るから下に変更
//        val tv = findViewById(R.id.mainStr)
//        tv.setText(displayedStr)
        mainStr.text = displayedStr

        return super.onOptionsItemSelected(item)

    }

    //itemが押されたら画面遷移
    override fun onItemClick(position: Int) {
        val memo = itemList.get(position)
        val intent = Intent(this@MainActivity, DetailActivity::class.java)
        intent.putExtra("updateDate", memo.updateDate)
        startActivity(intent)
    }

    override fun onItemCheckClick(position: Int, isChecked: Boolean) {
        val memo = results.get(position)
        //更新する
        realm.executeTransaction {
            memo?.ischeck = isChecked
        }
    }
}
