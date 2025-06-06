package vcmsa.projects.titanbudget1

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "TitanBudgetDB.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_ICON = "ICON"
        private const val TABLE_CATEGORY = "Category"
        private const val COLUMN_ICONID = "IconID"
        private const val COLUMN_ICONNAME = "IconName"
        private const val COLUMN_ICONURL = "IconUrl"
        private const val COLUMN_CATEGORYID = "CategoryID"
        private const val COLUMN_CATEGORYNAME = "CategoryName"
        private const val COLUMN_CATEGORYTYPE = "CategoryType"
        private const val COLUMN_ICONIDFK = "IconID"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLEICON = "CREATE TABLE $TABLE_ICON (" +
                "$COLUMN_ICONID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_ICONNAME TEXT, " +
                "$COLUMN_ICONURL TEXT)"
        db.execSQL(CREATE_TABLEICON)

        val CREATE_TABLECATEGORY = "CREATE TABLE $TABLE_CATEGORY (" +
                "$COLUMN_CATEGORYID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_CATEGORYNAME TEXT, " +
                "$COLUMN_CATEGORYTYPE TEXT," +
                "$COLUMN_ICONIDFK INTEGER, " +
                "FOREIGN KEY($COLUMN_ICONIDFK) REFERENCES $TABLE_ICON($COLUMN_ICONID))"
        db.execSQL(CREATE_TABLECATEGORY)

        val icons = listOf(
            "Exercise" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/running.png",
            "Bitcoin" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/bitcoin.png",
            "Vacation" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/beach-chair.png",
            "Cash" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/cash.png",
            "Investment" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/investment.png",
            "Dining" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/cutlery.png",
            "Education" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/scholarship.png",
            "Cosmetics" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/cosmetics.png",
            "Shopping" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/bag.png",
            "Alcohol" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/cocktail-glass.png",
            "House" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/icons8-home-100.png",
            "Debit" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/debit-card.png",
            "Savings" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/piggy-bank.png",
            "Hospital" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/heartbeat.png",
            "Entertainment" to "https://titanbudgeticonstorage.blob.core.windows.net/icons/theater.png"
        )

        val iconIdMap = mutableMapOf<String, Long>()

        for ((name, url) in icons) {
            val insertIconSQL = """
            INSERT INTO $TABLE_ICON ($COLUMN_ICONNAME, $COLUMN_ICONURL) 
            VALUES (?, ?)
        """.trimIndent()
            db.execSQL(insertIconSQL, arrayOf(name, url))
        }

        db?.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORYNAME, $COLUMN_CATEGORYTYPE, $COLUMN_ICONIDFK) VALUES ('Exercise', 'Expense', 1)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORYNAME, $COLUMN_CATEGORYTYPE, $COLUMN_ICONIDFK) VALUES ('Crypto', 'Income', 2)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORYNAME, $COLUMN_CATEGORYTYPE, $COLUMN_ICONIDFK) VALUES ('Vacation', 'Expense', 3)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORYNAME, $COLUMN_CATEGORYTYPE, $COLUMN_ICONIDFK) VALUES ('Cash', 'Income', 4)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORYNAME, $COLUMN_CATEGORYTYPE, $COLUMN_ICONIDFK) VALUES ('Investment', 'Income', 5)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORYNAME, $COLUMN_CATEGORYTYPE, $COLUMN_ICONIDFK) VALUES ('Dining', 'Expense', 6)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORYNAME, $COLUMN_CATEGORYTYPE, $COLUMN_ICONIDFK) VALUES ('Education', 'Expense', 7)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORYNAME, $COLUMN_CATEGORYTYPE, $COLUMN_ICONIDFK) VALUES ('Cosmetics', 'Expense', 8)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORYNAME, $COLUMN_CATEGORYTYPE, $COLUMN_ICONIDFK) VALUES ('Shopping', 'Expense', 9)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORYNAME, $COLUMN_CATEGORYTYPE, $COLUMN_ICONIDFK) VALUES ('Drinks', 'Expense', 10)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ICON")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORY")
        onCreate(db)
    }

    // Insert icon
    fun insertIcon(iconName: String, iconUrl: String) {
        val db = writableDatabase
        val query = "INSERT INTO $TABLE_ICON ($COLUMN_ICONNAME, $COLUMN_ICONURL) VALUES (?, ?)"
        db.execSQL(query, arrayOf(iconName, iconUrl))
        db.close()
    }

    // Insert category
    fun insertCategory(categoryName: String, categoryType: String, iconID: Int) {
        val db = writableDatabase
        val query = "INSERT INTO $TABLE_CATEGORY ($COLUMN_CATEGORYNAME, $COLUMN_CATEGORYTYPE, $COLUMN_ICONID) VALUES (?, ?,?)"
        db.execSQL(query, arrayOf(categoryName, categoryType, iconID))
        db.close()
    }

    // Get all icons
    @SuppressLint("Range")
    fun getAllIcons(): List<Icon> {
        val db = readableDatabase
        val icons = mutableListOf<Icon>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ICON", null)

        if (cursor.moveToFirst()) {
            do {
                val iconID = cursor.getInt(cursor.getColumnIndex(COLUMN_ICONID))
                val iconUrl = cursor.getString(cursor.getColumnIndex(COLUMN_ICONURL))
                icons.add(Icon(iconID,iconUrl))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return icons
    }


    // Get all categories
    @SuppressLint("Range")
    fun getAllCategories(): List<Category> {
        val db = readableDatabase
        val categories = mutableListOf<Category>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORY " +
                "JOIN $TABLE_ICON on Category.IconID = ICON.IconID", null)

        if (cursor.moveToFirst()) {
            do {
                val categoryID = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORYID))
                val categoryName = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORYNAME))
                val categoryType = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORYTYPE))
                val iconUrl = cursor.getString(cursor.getColumnIndex(COLUMN_ICONURL))
                categories.add(Category(categoryID, categoryName, categoryType, iconUrl))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return categories
    }

    // Get count of all icons
    fun getIconCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_ICON", null)
        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        return count
    }

    // Get count of all categories
    fun getCategoriesCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_CATEGORY", null)
        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        return count
    }


}