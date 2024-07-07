package com.example.pockeitt.utils

object ListDataPump {
    var data: HashMap<String, MutableList<String>>
        private set

    init {
        data = HashMap()
        val bills: MutableList<String> = ArrayList()
        val needs: MutableList<String> = ArrayList()

        val wants: MutableList<String> = ArrayList()

        val savings: MutableList<String> = ArrayList()

        data["Bills"] = bills
        data["Needs"] = needs
        data["Wants"] = wants
        data["Savings"] = savings
    }

    fun getList(key: String): List<String> {
        return data[key]!!
    }

    fun addItem(key: String, item: String) {
        var list = data[key]
        if (list != null) {
            list.add(item)
        } else {
            list = ArrayList()
            list.add(item)
            data[key] = list
        }
    }

    fun removeItem(key: String, item: String) {
        val list = data[key]
        list?.remove(item)
    }

    fun updateItem(key: String, index: Int, newItem: String) {
        val list = data[key]
        if (list != null && index >= 0 && index < list.size) {
            list[index] = newItem
        }
    }
}