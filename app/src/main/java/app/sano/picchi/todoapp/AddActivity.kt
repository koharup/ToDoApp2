package app.sano.picchi.todoapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_add.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream


class AddActivity : AppCompatActivity() {
    //realm型の変数を宣言
    lateinit var realm: Realm

    //EditText型の変数宣言
    lateinit var titleEditText: EditText
    lateinit var contentEditText: EditText

    //カメラ
    lateinit var pictureUri: Uri
    var bitmap:Bitmap? = null

    //アルバムとカメラ
    companion object {
        const val REQUEST_CODE_PHOTO: Int = 123
        const val REQUEST_CODE_PERMISSION: Int = 100
        const val REQUEST_CODE_CAMERA: Int = 200
    }


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
            memo.bitmap = createImageData(bitmap!!)


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

        if (bitmap != null) {
            //出力してみる
            check(title, updateDate, content)
            //保存
            save(title, updateDate, content)
            //画面を終了
            finish()
        }

    }

    fun check(title: String, updateDate: String, content: String) {

        //メモのクラスのインスタンスを生成する
        val memo = Memo()

        //それぞれの要素を代入
        memo.title = title
        memo.updateDate = updateDate
        memo.content = content
        memo.bitmap = createImageData(bitmap!!)


        //ログに出してみる
        Log.d("Memo", memo.title)
        Log.d("Memo", memo.updateDate)
        Log.d("Memo", memo.content)
    }

    fun createImageData(bitmap: Bitmap) : ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val imageByteArray = baos.toByteArray()
        return imageByteArray
    }


    override fun onDestroy() {
        super.onDestroy()

        //realmを閉じる
        realm.close()
    }

    //メニューのとこ
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater

        inflater.inflate(app.sano.picchi.todoapp.R.menu.camera_item_memo, menu)

        return super.onCreateOptionsMenu(menu)

    }

    //メニューのとこ
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.getItemId()
        var displayedStr: String? = null // 画面に表示する文字列

        when (itemId) {
            app.sano.picchi.todoapp.R.id.camera_menu_1 -> {
                cameraTask()


            }
            app.sano.picchi.todoapp.R.id.camera_menu_2 -> {
                val intent: Intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_CODE_PHOTO)


            }
        }


        return super.onOptionsItemSelected(item)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK) {
            pictureUri = data?.data!!
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, pictureUri)
            picImage.setImageBitmap(bitmap)
        }

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, pictureUri)
            picImage.setImageBitmap(bitmap)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION) {

            // requestPermissionsで設定した順番で結果が格納されています。
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 許可されたので処理を続行
                takePicture()
            } else {
                // パーミッションのリクエストに対して「許可しない」
                // または以前のリクエストで「二度と表示しない」にチェックを入れられた状態で
                // 「許可しない」を押されていると、必ずここに呼び出されます。
                Toast.makeText(this, "パーミッションが許可されていません。設定から許可してください。", Toast.LENGTH_SHORT).show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun cameraTask() {
        checkPermission(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
        if (checkPermission(Manifest.permission.CAMERA) && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            takePicture()
        }
    }

    fun checkPermission(tergerPermissionArray: Array<String>) {

        var chechNeededPermissionList = mutableListOf<String>()

        for (tergerPermission in tergerPermissionArray) {
            if (!checkPermission(tergerPermission)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, tergerPermission)) {
                    // すでに１度パーミッションのリクエストが行われていて、
                    // ユーザーに「許可しない（二度と表示しないは非チェック）」をされていると
                    // この処理が呼ばれます。
                    Toast.makeText(this, "パーミッションが許可されていません。設定から許可してください。", Toast.LENGTH_SHORT)
                        .show()
                    return
                } else {
                    chechNeededPermissionList.add(tergerPermission)
                }
            }
        }

        if (!chechNeededPermissionList.isEmpty()) {
            ActivityCompat.requestPermissions(
                this, chechNeededPermissionList.toTypedArray(), REQUEST_CODE_PERMISSION
            )
        }
    }


    fun checkPermission(tergerPermission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            tergerPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun takePicture() {
        val fileName: String = "${System.currentTimeMillis()}.jpg"
        val contentValues: ContentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, fileName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        pictureUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!


        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }

}









