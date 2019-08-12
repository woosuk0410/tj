package com.example.android.tj.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// have to store blob in different chunks due to limit of cursor window size (2MB)
@Entity(tableName = "songs")
data class Song(
        @PrimaryKey
        val id: String,
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val data0: ByteArray = ByteArray(0),
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val data1: ByteArray = ByteArray(0),
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val data2: ByteArray = ByteArray(0),
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val data3: ByteArray = ByteArray(0),
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val data4: ByteArray = ByteArray(0),
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val data5: ByteArray = ByteArray(0),
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val data6: ByteArray = ByteArray(0),
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val data7: ByteArray = ByteArray(0),
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val data8: ByteArray = ByteArray(0),
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val data9: ByteArray = ByteArray(0)
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data10: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data11: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data12: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data13: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data14: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data15: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data16: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data17: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data18: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data19: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data20: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data21: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data22: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data23: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data24: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data25: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data26: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data27: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data28: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data29: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data30: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data31: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data32: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data33: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data34: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data35: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data36: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data37: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data38: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data39: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data40: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data41: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data42: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data43: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data44: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data45: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data46: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data47: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data48: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data49: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data50: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data51: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data52: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data53: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data54: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data55: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data56: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data57: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data58: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data59: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data60: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data61: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data62: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data63: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data64: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data65: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data66: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data67: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data68: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data69: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data70: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data71: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data72: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data73: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data74: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data75: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data76: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data77: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data78: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data79: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data80: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data81: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data82: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data83: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data84: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data85: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data86: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data87: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data88: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data89: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data90: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data91: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data92: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data93: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data94: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data95: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data96: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data97: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data98: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data99: ByteArray = ByteArray(0),
//        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//        val data100: ByteArray = ByteArray(0)
) {
    fun data(): ByteArray {
        return data0 + data1 + data2 + data3 + data4 + data5 + data6 + data7 + data8 + data9
//            + data10
//            + data11 + data12 + data13 + data14 + data15 + data16 + data17 + data18 + data19 + data20 + data21 + data22 + data23 + data24 + data25 + data26 + data27 + data28 + data29 + data30 + data31 + data32 + data33 + data34 + data35 + data36 + data37 + data38 + data39 + data40 + data41 + data42 + data43 + data44 + data45 + data46 + data47 + data48 + data49 + data50 + data51 + data52 + data53 + data54 + data55 + data56 + data57 + data58 + data59 + data60 + data61 + data62 + data63 + data64 + data65 + data66 + data67 + data68 + data69 + data70 + data71 + data72 + data73 + data74 + data75 + data76 + data77 + data78 + data79 + data80 + data81 + data82 + data83 + data84 + data85 + data86 + data87 + data88 + data89 + data90 + data91 + data92 + data93 + data94 + data95 + data96 + data97 + data98 + data99
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Song

        if (id != other.id) return false
        if (!data().contentEquals(other.data())) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + data().contentHashCode()
        return result
    }
}
