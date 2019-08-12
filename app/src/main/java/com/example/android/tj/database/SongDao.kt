package com.example.android.tj.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SongDao {

    @Query("SELECT data0 FROM songs WHERE id =:id")
    fun getData0ById(id: String): ByteArray

    @Query("SELECT data1 FROM songs WHERE id =:id")
    fun getData1ById(id: String): ByteArray

    @Query("SELECT data2 FROM songs WHERE id =:id")
    fun getData2ById(id: String): ByteArray

    @Query("SELECT data3 FROM songs WHERE id =:id")
    fun getData3ById(id: String): ByteArray

    @Query("SELECT data4 FROM songs WHERE id =:id")
    fun getData4ById(id: String): ByteArray

    @Query("SELECT data5 FROM songs WHERE id =:id")
    fun getData5ById(id: String): ByteArray

    @Query("SELECT data6 FROM songs WHERE id =:id")
    fun getData6ById(id: String): ByteArray

    @Query("SELECT data7 FROM songs WHERE id =:id")
    fun getData7ById(id: String): ByteArray

    @Query("SELECT data8 FROM songs WHERE id =:id")
    fun getData8ById(id: String): ByteArray

    @Query("SELECT data9 FROM songs WHERE id =:id")
    fun getData9ById(id: String): ByteArray
//
//    @Query("SELECT data10 FROM songs WHERE id =:id")
//    fun getData10ById(id: String): ByteArray
//
//    @Query("SELECT data11 FROM songs WHERE id =:id")
//    fun getData11ById(id: String): ByteArray
//
//    @Query("SELECT data12 FROM songs WHERE id =:id")
//    fun getData12ById(id: String): ByteArray
//
//    @Query("SELECT data13 FROM songs WHERE id =:id")
//    fun getData13ById(id: String): ByteArray
//
//    @Query("SELECT data14 FROM songs WHERE id =:id")
//    fun getData14ById(id: String): ByteArray
//
//    @Query("SELECT data15 FROM songs WHERE id =:id")
//    fun getData15ById(id: String): ByteArray
//
//    @Query("SELECT data16 FROM songs WHERE id =:id")
//    fun getData16ById(id: String): ByteArray
//
//    @Query("SELECT data17 FROM songs WHERE id =:id")
//    fun getData17ById(id: String): ByteArray
//
//    @Query("SELECT data18 FROM songs WHERE id =:id")
//    fun getData18ById(id: String): ByteArray
//
//    @Query("SELECT data19 FROM songs WHERE id =:id")
//    fun getData19ById(id: String): ByteArray
//
//    @Query("SELECT data20 FROM songs WHERE id =:id")
//    fun getData20ById(id: String): ByteArray
//
//    @Query("SELECT data21 FROM songs WHERE id =:id")
//    fun getData21ById(id: String): ByteArray
//
//    @Query("SELECT data22 FROM songs WHERE id =:id")
//    fun getData22ById(id: String): ByteArray
//
//    @Query("SELECT data23 FROM songs WHERE id =:id")
//    fun getData23ById(id: String): ByteArray
//
//    @Query("SELECT data24 FROM songs WHERE id =:id")
//    fun getData24ById(id: String): ByteArray
//
//    @Query("SELECT data25 FROM songs WHERE id =:id")
//    fun getData25ById(id: String): ByteArray
//
//    @Query("SELECT data26 FROM songs WHERE id =:id")
//    fun getData26ById(id: String): ByteArray
//
//    @Query("SELECT data27 FROM songs WHERE id =:id")
//    fun getData27ById(id: String): ByteArray
//
//    @Query("SELECT data28 FROM songs WHERE id =:id")
//    fun getData28ById(id: String): ByteArray
//
//    @Query("SELECT data29 FROM songs WHERE id =:id")
//    fun getData29ById(id: String): ByteArray
//
//    @Query("SELECT data30 FROM songs WHERE id =:id")
//    fun getData30ById(id: String): ByteArray
//
//    @Query("SELECT data31 FROM songs WHERE id =:id")
//    fun getData31ById(id: String): ByteArray
//
//    @Query("SELECT data32 FROM songs WHERE id =:id")
//    fun getData32ById(id: String): ByteArray
//
//    @Query("SELECT data33 FROM songs WHERE id =:id")
//    fun getData33ById(id: String): ByteArray
//
//    @Query("SELECT data34 FROM songs WHERE id =:id")
//    fun getData34ById(id: String): ByteArray
//
//    @Query("SELECT data35 FROM songs WHERE id =:id")
//    fun getData35ById(id: String): ByteArray
//
//    @Query("SELECT data36 FROM songs WHERE id =:id")
//    fun getData36ById(id: String): ByteArray
//
//    @Query("SELECT data37 FROM songs WHERE id =:id")
//    fun getData37ById(id: String): ByteArray
//
//    @Query("SELECT data38 FROM songs WHERE id =:id")
//    fun getData38ById(id: String): ByteArray
//
//    @Query("SELECT data39 FROM songs WHERE id =:id")
//    fun getData39ById(id: String): ByteArray
//
//    @Query("SELECT data40 FROM songs WHERE id =:id")
//    fun getData40ById(id: String): ByteArray
//
//    @Query("SELECT data41 FROM songs WHERE id =:id")
//    fun getData41ById(id: String): ByteArray
//
//    @Query("SELECT data42 FROM songs WHERE id =:id")
//    fun getData42ById(id: String): ByteArray
//
//    @Query("SELECT data43 FROM songs WHERE id =:id")
//    fun getData43ById(id: String): ByteArray
//
//    @Query("SELECT data44 FROM songs WHERE id =:id")
//    fun getData44ById(id: String): ByteArray
//
//    @Query("SELECT data45 FROM songs WHERE id =:id")
//    fun getData45ById(id: String): ByteArray
//
//    @Query("SELECT data46 FROM songs WHERE id =:id")
//    fun getData46ById(id: String): ByteArray
//
//    @Query("SELECT data47 FROM songs WHERE id =:id")
//    fun getData47ById(id: String): ByteArray
//
//    @Query("SELECT data48 FROM songs WHERE id =:id")
//    fun getData48ById(id: String): ByteArray
//
//    @Query("SELECT data49 FROM songs WHERE id =:id")
//    fun getData49ById(id: String): ByteArray
//
//    @Query("SELECT data50 FROM songs WHERE id =:id")
//    fun getData50ById(id: String): ByteArray
//
//    @Query("SELECT data51 FROM songs WHERE id =:id")
//    fun getData51ById(id: String): ByteArray
//
//    @Query("SELECT data52 FROM songs WHERE id =:id")
//    fun getData52ById(id: String): ByteArray
//
//    @Query("SELECT data53 FROM songs WHERE id =:id")
//    fun getData53ById(id: String): ByteArray
//
//    @Query("SELECT data54 FROM songs WHERE id =:id")
//    fun getData54ById(id: String): ByteArray
//
//    @Query("SELECT data55 FROM songs WHERE id =:id")
//    fun getData55ById(id: String): ByteArray
//
//    @Query("SELECT data56 FROM songs WHERE id =:id")
//    fun getData56ById(id: String): ByteArray
//
//    @Query("SELECT data57 FROM songs WHERE id =:id")
//    fun getData57ById(id: String): ByteArray
//
//    @Query("SELECT data58 FROM songs WHERE id =:id")
//    fun getData58ById(id: String): ByteArray
//
//    @Query("SELECT data59 FROM songs WHERE id =:id")
//    fun getData59ById(id: String): ByteArray
//
//    @Query("SELECT data60 FROM songs WHERE id =:id")
//    fun getData60ById(id: String): ByteArray
//
//    @Query("SELECT data61 FROM songs WHERE id =:id")
//    fun getData61ById(id: String): ByteArray
//
//    @Query("SELECT data62 FROM songs WHERE id =:id")
//    fun getData62ById(id: String): ByteArray
//
//    @Query("SELECT data63 FROM songs WHERE id =:id")
//    fun getData63ById(id: String): ByteArray
//
//    @Query("SELECT data64 FROM songs WHERE id =:id")
//    fun getData64ById(id: String): ByteArray
//
//    @Query("SELECT data65 FROM songs WHERE id =:id")
//    fun getData65ById(id: String): ByteArray
//
//    @Query("SELECT data66 FROM songs WHERE id =:id")
//    fun getData66ById(id: String): ByteArray
//
//    @Query("SELECT data67 FROM songs WHERE id =:id")
//    fun getData67ById(id: String): ByteArray
//
//    @Query("SELECT data68 FROM songs WHERE id =:id")
//    fun getData68ById(id: String): ByteArray
//
//    @Query("SELECT data69 FROM songs WHERE id =:id")
//    fun getData69ById(id: String): ByteArray
//
//    @Query("SELECT data70 FROM songs WHERE id =:id")
//    fun getData70ById(id: String): ByteArray
//
//    @Query("SELECT data71 FROM songs WHERE id =:id")
//    fun getData71ById(id: String): ByteArray
//
//    @Query("SELECT data72 FROM songs WHERE id =:id")
//    fun getData72ById(id: String): ByteArray
//
//    @Query("SELECT data73 FROM songs WHERE id =:id")
//    fun getData73ById(id: String): ByteArray
//
//    @Query("SELECT data74 FROM songs WHERE id =:id")
//    fun getData74ById(id: String): ByteArray
//
//    @Query("SELECT data75 FROM songs WHERE id =:id")
//    fun getData75ById(id: String): ByteArray
//
//    @Query("SELECT data76 FROM songs WHERE id =:id")
//    fun getData76ById(id: String): ByteArray
//
//    @Query("SELECT data77 FROM songs WHERE id =:id")
//    fun getData77ById(id: String): ByteArray
//
//    @Query("SELECT data78 FROM songs WHERE id =:id")
//    fun getData78ById(id: String): ByteArray
//
//    @Query("SELECT data79 FROM songs WHERE id =:id")
//    fun getData79ById(id: String): ByteArray
//
//    @Query("SELECT data80 FROM songs WHERE id =:id")
//    fun getData80ById(id: String): ByteArray
//
//    @Query("SELECT data81 FROM songs WHERE id =:id")
//    fun getData81ById(id: String): ByteArray
//
//    @Query("SELECT data82 FROM songs WHERE id =:id")
//    fun getData82ById(id: String): ByteArray
//
//    @Query("SELECT data83 FROM songs WHERE id =:id")
//    fun getData83ById(id: String): ByteArray
//
//    @Query("SELECT data84 FROM songs WHERE id =:id")
//    fun getData84ById(id: String): ByteArray
//
//    @Query("SELECT data85 FROM songs WHERE id =:id")
//    fun getData85ById(id: String): ByteArray
//
//    @Query("SELECT data86 FROM songs WHERE id =:id")
//    fun getData86ById(id: String): ByteArray
//
//    @Query("SELECT data87 FROM songs WHERE id =:id")
//    fun getData87ById(id: String): ByteArray
//
//    @Query("SELECT data88 FROM songs WHERE id =:id")
//    fun getData88ById(id: String): ByteArray
//
//    @Query("SELECT data89 FROM songs WHERE id =:id")
//    fun getData89ById(id: String): ByteArray
//
//    @Query("SELECT data90 FROM songs WHERE id =:id")
//    fun getData90ById(id: String): ByteArray
//
//    @Query("SELECT data91 FROM songs WHERE id =:id")
//    fun getData91ById(id: String): ByteArray
//
//    @Query("SELECT data92 FROM songs WHERE id =:id")
//    fun getData92ById(id: String): ByteArray
//
//    @Query("SELECT data93 FROM songs WHERE id =:id")
//    fun getData93ById(id: String): ByteArray
//
//    @Query("SELECT data94 FROM songs WHERE id =:id")
//    fun getData94ById(id: String): ByteArray
//
//    @Query("SELECT data95 FROM songs WHERE id =:id")
//    fun getData95ById(id: String): ByteArray
//
//    @Query("SELECT data96 FROM songs WHERE id =:id")
//    fun getData96ById(id: String): ByteArray
//
//    @Query("SELECT data97 FROM songs WHERE id =:id")
//    fun getData97ById(id: String): ByteArray
//
//    @Query("SELECT data98 FROM songs WHERE id =:id")
//    fun getData98ById(id: String): ByteArray
//
//    @Query("SELECT data99 FROM songs WHERE id =:id")
//    fun getData99ById(id: String): ByteArray
}