package app.sano.picchi.todoapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_add.*

class DetailActivity : AppCompatActivity() {

    //realm型の変数を宣言
    lateinit var realm: Realm

    //EditText型の変数宣言
    lateinit var titleEditText: EditText
    lateinit var contentEditText: EditText

    lateinit var bitmap: Bitmap



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
        bitmap = BitmapFactory.decodeByteArray(memo?.bitmap, 0, memo?.bitmap?.size!!)
        picImage.setImageBitmap(bitmap)

    }

    fun update(view: View) {
        val memo = realm.where(Memo::class.java).equalTo(
            "updateDate",
            this.intent.getStringExtra("updateDate")
        ).findFirst()

        //更新する
        realm.executeTransaction {
            memo?.title = titleEditText.text.toString()
            memo?.content = contentEditText.text.toString()
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
