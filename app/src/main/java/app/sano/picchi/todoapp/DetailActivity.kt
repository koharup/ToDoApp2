package app.sano.picchi.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import io.realm.Realm
import kotlinx.android.synthetic.main.layout_item_memo.*

class DetailActivity : AppCompatActivity() {

    //realm型の変数を宣言
    lateinit var realm: Realm

    //EditText型の変数宣言
    lateinit var titleEditText: EditText
    lateinit var contentEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //realmを開く
        realm = Realm.getDefaultInstance()

        titleEditText = findViewById(R.id.titleEditText) as EditText
        contentEditText = findViewById(R.id.contentEditText) as EditText
        showData()


    }

    fun showData() {
        val memo
                = realm.where(Memo::class.java).equalTo(
            "updateDate",
            this.intent.getStringExtra("updateDate")
        ).findFirst()

        titleEditText.setText(memo?.title)
        contentEditText.setText(memo?.content)
    }

    fun update(view: View) {
        val memo = realm.where(Memo::class.java).equalTo(
            "updateDate",
            this.intent.getStringExtra("updateDate")
        ).findFirst()

        //更新する
        realm.executeTransaction {
            memo?.title = titleText.text.toString()
            memo?.content = contentText.text.toString()
        }

        //画面を閉じる
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        //realmを閉じる
        realm.close()
    }


}
