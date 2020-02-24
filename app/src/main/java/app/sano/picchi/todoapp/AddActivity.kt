package app.sano.picchi.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.*
import android.R
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import android.view.MenuInflater

class AddActivity : AppCompatActivity() {
    //realm型の変数を宣言
    lateinit var realm: Realm

    //EditText型の変数宣言
    lateinit var titleEditText: EditText
    lateinit var contentEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(app.sano.picchi.todoapp.R.layout.activity_add)

        //realmを開く
        Realm.init(this)
        realm = Realm.getDefaultInstance()

        //関連付け
        titleEditText = findViewById(app.sano.picchi.todoapp.R.id.titleEditText) as EditText
        contentEditText = findViewById(app.sano.picchi.todoapp.R.id.contentEditText) as EditText



    }

    fun save(title: String, updateDate: String, content: String) {

        //メモを保存
        realm.executeTransaction {
            val memo = realm.createObject(Memo::class.java)
            memo.title = title
            memo.updateDate = updateDate
            memo.content = content
            memo.ischeck = false


        }
    }

    fun create(view: View) {
        //タイトル取得
        val title = titleEditText.text.toString()

        //日付を取得
        val date = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPANESE)
        val updateDate = sdf.format(date)

        //内容の取得
        val content = contentEditText.text.toString()



        //出力してみる
        check(title, updateDate, content)


        //保存
        save(title, updateDate, content)

        //画面を終了
        finish()

    }

    fun check(title: String, updateDate: String, content: String) {

        //メモのクラスのインスタンスを生成する
        val memo = Memo()

        //それぞれの要素を代入
        memo.title = title
        memo.updateDate = updateDate
        memo.content = content


        //ログに出してみる
        Log.d("Memo", memo.title)
        Log.d("Memo", memo.updateDate)
        Log.d("Memo", memo.content)

    }

    override fun onDestroy() {
        super.onDestroy()

        //realmを閉じる
        realm.close()
    }
}









