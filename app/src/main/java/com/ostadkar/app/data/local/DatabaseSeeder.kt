package com.ostadkar.app.data.local

import com.ostadkar.app.data.local.dao.WorkLocationDao
import com.ostadkar.app.data.local.dao.WorkTypeDao
import com.ostadkar.app.data.local.entity.WorkLocationEntity
import com.ostadkar.app.data.local.entity.WorkTypeEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val workTypeDao: WorkTypeDao,
    private val workLocationDao: WorkLocationDao
) {
    suspend fun seedIfNeeded() {
        if (workTypeDao.count() > 0) return

        // 1. کاشی‌کاری
        val tileId = workTypeDao.insert(
            WorkTypeEntity(code = "tile", nameFa = "کاشی‌کاری", sortOrder = 1)
        )
        seedLocations(tileId, listOf(
            "floor" to "کف",
            "wall" to "دیوار",
            "bathroom_floor" to "کف حمام",
            "bathroom_wall" to "دیوار حمام",
            "wc_floor" to "کف سرویس",
            "wc_wall" to "دیوار سرویس",
            "kitchen" to "آشپزخانه",
            "backsplash" to "بین کابینتی",
            "terrace" to "تراس",
            "parking" to "پارکینگ",
            "staircase" to "راه‌پله",
            "yard" to "حیاط"
        ))

        // 2. سرامیک‌کاری
        val ceramicId = workTypeDao.insert(
            WorkTypeEntity(code = "ceramic", nameFa = "سرامیک‌کاری", sortOrder = 2)
        )
        seedLocations(ceramicId, listOf(
            "floor" to "کف",
            "wall" to "دیوار",
            "bathroom_floor" to "کف حمام",
            "bathroom_wall" to "دیوار حمام",
            "wc_floor" to "کف سرویس",
            "wc_wall" to "دیوار سرویس",
            "kitchen" to "آشپزخانه",
            "terrace" to "تراس",
            "parking" to "پارکینگ",
            "staircase" to "راه‌پله",
            "yard" to "حیاط"
        ))

        // 3. اسلب سرامیک
        val slabCeramicId = workTypeDao.insert(
            WorkTypeEntity(code = "slab_ceramic", nameFa = "اسلب سرامیک", sortOrder = 3)
        )
        seedLocations(slabCeramicId, listOf(
            "floor" to "کف",
            "wall" to "دیوار",
            "kitchen" to "آشپزخانه",
            "backsplash" to "بین کابینتی",
            "wc" to "سرویس",
            "bathroom" to "حمام",
            "living" to "پذیرایی",
            "lobby" to "لابی",
            "staircase" to "راه‌پله",
            "counter" to "میز و کانتر"
        ))

        // 4. اسلب سنگ
        val slabStoneId = workTypeDao.insert(
            WorkTypeEntity(code = "slab_stone", nameFa = "اسلب سنگ", sortOrder = 4)
        )
        seedLocations(slabStoneId, listOf(
            "floor" to "کف",
            "wall" to "دیوار",
            "lobby" to "لابی",
            "living" to "پذیرایی",
            "staircase" to "راه‌پله",
            "counter" to "کانتر",
            "cabinet" to "کابینت",
            "wc" to "سرویس",
            "bathroom" to "حمام",
            "facade" to "نما"
        ))

        // 5. سنگ نما
        val stoneFacadeId = workTypeDao.insert(
            WorkTypeEntity(code = "stone_facade", nameFa = "سنگ نما", sortOrder = 5)
        )
        seedLocations(stoneFacadeId, listOf(
            "main_facade" to "نمای اصلی",
            "side_facade" to "نمای جانبی",
            "entrance" to "ورودی ساختمان",
            "column" to "ستون",
            "window_frame" to "قاب پنجره",
            "door_frame" to "قاب درب",
            "yard_wall" to "دیوار حیاط",
            "parking" to "پارکینگ",
            "staircase" to "راه‌پله"
        ))

        // 6. نمای رومی
        val romanFacadeId = workTypeDao.insert(
            WorkTypeEntity(code = "roman_facade", nameFa = "نمای رومی", sortOrder = 6)
        )
        seedLocations(romanFacadeId, listOf(
            "main_facade" to "نمای اصلی",
            "column" to "ستون",
            "capital" to "سرستون",
            "window_frame" to "قاب پنجره",
            "door_frame" to "قاب درب",
            "cornice" to "تاج نما",
            "molding" to "ابزار نما",
            "entrance" to "ورودی ساختمان"
        ))

        // 7. آجر نما
        val brickFacadeId = workTypeDao.insert(
            WorkTypeEntity(code = "brick_facade", nameFa = "آجر نما", sortOrder = 7)
        )
        seedLocations(brickFacadeId, listOf(
            "main_facade" to "نمای اصلی",
            "side_facade" to "نمای جانبی",
            "yard_wall" to "دیوار حیاط",
            "entrance" to "ورودی",
            "window_frame" to "قاب پنجره",
            "door_frame" to "قاب درب",
            "column" to "ستون"
        ))

        // 8. نمای ترکیبی آجر و سنگ
        val mixedFacadeId = workTypeDao.insert(
            WorkTypeEntity(code = "mixed_facade", nameFa = "نمای ترکیبی آجر و سنگ", sortOrder = 8)
        )
        seedLocations(mixedFacadeId, listOf(
            "main_facade" to "نمای اصلی",
            "side_facade" to "نمای جانبی",
            "entrance" to "ورودی",
            "window_frame" to "قاب پنجره",
            "door_frame" to "قاب درب",
            "column" to "ستون",
            "yard_wall" to "دیوار حیاط"
        ))
    }

    private suspend fun seedLocations(workTypeId: Long, items: List<Pair<String, String>>) {
        val entities = items.mapIndexed { index, (code, name) ->
            WorkLocationEntity(
                workTypeId = workTypeId,
                code = code,
                nameFa = name,
                sortOrder = index + 1
            )
        }
        workLocationDao.insertAll(entities)
    }
}
