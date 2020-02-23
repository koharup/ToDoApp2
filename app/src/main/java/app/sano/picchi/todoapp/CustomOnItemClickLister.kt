package app.sano.picchi.todoapp

interface CustomOnItemClickLister {
    fun onItemClick(position: Int)
    fun onItemCheckClick(position: Int, isChecked: Boolean)
}