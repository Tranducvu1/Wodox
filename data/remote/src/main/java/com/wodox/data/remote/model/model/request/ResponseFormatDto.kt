package com.wodox.data.remote.model.model.request

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import com.starnest.domain.remote.model.response.Properties
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ResponseFormatDto(
    @SerializedName("json_schema")
    var jsonSchema: JsonSchemaDto,
    @SerializedName("type")
    var type: String? = "json_schema"
) : java.io.Serializable {

}

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class JsonSchemaDto(
    @SerializedName("name")
    var name: String,
    @SerializedName("schema")
    var schema: SchemaDto,
    @SerializedName("strict")
    var strict: Boolean? = true
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SchemaDto(
    @SerializedName("additionalProperties")
    var additionalProperties: Boolean? = false,
    @SerializedName("properties")
    var properties: Properties,
    @SerializedName("required")
    var required: List<String>,
    @SerializedName("type")
    var type: String? = "object"
)


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ChatSuggestionItemsDto(
    @SerializedName("type")
    var type: String? = "string"
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ChatSuggestionDto(
    @SerializedName("items")
    var items: ChatSuggestionItemsDto = ChatSuggestionItemsDto(),
    @SerializedName("type")
    var type: String? = "array"
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SuggestionPropertiesDto(
    @SerializedName("suggestions")
    var suggestions: ChatSuggestionDto = ChatSuggestionDto()
) : Properties


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class QuizAnswerItemsDto(
    @SerializedName("type")
    var type: String? = "string"
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class QuizAnswerPropertiesDto(
    @SerializedName("items")
    var items: QuizAnswerItemsDto = QuizAnswerItemsDto(),
    @SerializedName("type")
    var type: String? = "array"
): Properties

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class QuizQuestionItems(
    @SerializedName("question")
    var question: TypeDto? = TypeDto(),
    @SerializedName("answers")
    var answers: QuizAnswerPropertiesDto? = QuizAnswerPropertiesDto(),
    @SerializedName("correctAnswer")
    var correctAnswer: TypeDto? = TypeDto(),
): Properties

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class QuizQuestionPropertiesDto(
    @SerializedName("items")
    var items: QuizQuestionItems = QuizQuestionItems(),
    @SerializedName("type")
    var type: String? = "array"
): Properties


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class QuizPropertiesDto(
    @SerializedName("questions")
    var questions: QuizQuestionPropertiesDto = QuizQuestionPropertiesDto()
) : Properties



@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SummaryKeypointLanguageProperties(
    @SerializedName("summary")
    var summary: TypeDto? = TypeDto(),
    @SerializedName("keypoint")
    var keypoint: TypeDto? = TypeDto(),
    @SerializedName("languageCode")
    var languageCode: TypeDto? = TypeDto(),
) : Properties


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class StorytellingProperties(
    @SerializedName("title")
    var title: TypeDto? = TypeDto(),
    @SerializedName("content")
    var content: TypeDto? = TypeDto()
) : Properties



@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CheckViolateAndTranslatePropertiesDto(
    @SerializedName("is_correct")
    var isCorrect: TypeDto? = TypeDto(
        type = "boolean",
        description = "The final answer that sentences have any sexual words"
    ),
    @SerializedName("translated")
    var translated: TypeDto? = TypeDto(
        type = "string",
        description = "translate prompt translate to english"
    )
) : Properties


data class CheckViolateAndTranslate(
    @SerializedName("is_correct")
    var isViolate: Boolean,
    @SerializedName("translated")
    var translated: String
)


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SynonymsPropertiesDto(
    @SerializedName("synonyms")
    var synonyms: SynonymsDto = SynonymsDto()
) : Properties


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class AutoGrammarProperties(
    @SerializedName("improved")
    var improved: TypeDto? = TypeDto(),
    @SerializedName("isCorrect")
    var isCorrect: TypeDto? = TypeDto(
        type = "boolean",
        description = "The final answer that sentences is correct grammar."
    )
) : Properties


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TranslatorPropertiesDto(
    @SerializedName("result")
    var result: TypeDto? = TypeDto(),
    @SerializedName("languageDetected")
    var languageDetected: TypeDto? = TypeDto(
        type = "string",
        description = "The language detected from source language."
    ),
    @SerializedName("languageDetectedCode")
    var languageDetectedCode: TypeDto? = TypeDto(
        type = "string",
        description = "The language code detected from source language."
    )
) : Properties


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class GrammarPropertiesDto(
    @SerializedName("explanation")
    var explanation: TypeDto? = TypeDto(),
    @SerializedName("improved")
    var improved: TypeDto? = TypeDto(),
    @SerializedName("original")
    var original: TypeDto? = TypeDto(),
    @SerializedName("isCorrect")
    var isCorrect: TypeDto? = TypeDto(
        type = "boolean",
        description = "The final answer that sentences is correct grammar."
    )
) : Properties


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SynonymsDto(
    @SerializedName("items")
    var items: ItemsDto = ItemsDto(),
    @SerializedName("type")
    var type: String? = "array"
)


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ItemsDto(
    @SerializedName("additionalProperties")
    var additionalProperties: Boolean? = false,
    @SerializedName("properties")
    var properties: SynonymPropertiesDto? = SynonymPropertiesDto(),
    @SerializedName("required")
    var required: List<String> = listOf("example", "explain", "word"),
    @SerializedName("type")
    var type: String? = "object"
)


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SynonymPropertiesDto(
    @SerializedName("example")
    var example: TypeDto? = TypeDto(),
    @SerializedName("explain")
    var explain: TypeDto? = TypeDto(),
    @SerializedName("word")
    var word: TypeDto? = TypeDto()
)


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TypeDto(
    @SerializedName("type")
    var type: String? = "string",
    @SerializedName("description")
    var description: String? = null
)
