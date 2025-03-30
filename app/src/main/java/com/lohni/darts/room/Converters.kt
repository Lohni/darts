package com.lohni.darts.room

import androidx.room.TypeConverter
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.room.enums.GameModeType
import com.lohni.darts.room.enums.ScoreModifier
import com.lohni.darts.room.enums.ScoreType
import com.lohni.darts.room.enums.SettingType
import com.lohni.darts.room.enums.StepWinCondition
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): String {
        return value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun fromField(value: Field): Int {
        return value.fId
    }

    @TypeConverter
    fun toField(value: Int): Field {
        return Field.getFieldByInt(value)
    }

    @TypeConverter
    fun fromFieldType(value: FieldType): Int {
        return value.ftId
    }

    @TypeConverter
    fun toFieldType(value: Int): FieldType {
        return FieldType.getFieldTypeByInt(value)
    }

    @TypeConverter
    fun fromScoreModifier(value: ScoreModifier): Int {
        return value.smId
    }

    @TypeConverter
    fun toScoreModifier(value: Int): ScoreModifier {
        return ScoreModifier.getScoreModifierByInt(value)
    }

    @TypeConverter
    fun fromScoreType(value: ScoreType): Int {
        return value.stId
    }

    @TypeConverter
    fun toScoreType(value: Int): ScoreType {
        return ScoreType.getScoreTypeByInt(value)
    }

    @TypeConverter
    fun fromSettingType(value: SettingType): Int {
        return value.stId
    }

    @TypeConverter
    fun toSettingType(value: Int): SettingType {
        return SettingType.getSettingTypeByInt(value)
    }

    @TypeConverter
    fun fromGameModeType(value: GameModeType): Int {
        return value.gmtId
    }

    @TypeConverter
    fun toGameModeType(value: Int): GameModeType {
        return GameModeType.getGameModeTypeByInt(value)
    }

    @TypeConverter
    fun fromStepWinCondition(value: StepWinCondition): Int {
        return value.swcId
    }

    @TypeConverter
    fun toStepWinCondition(value: Int): StepWinCondition {
        return StepWinCondition.getStepWinCondition(value)
    }
}