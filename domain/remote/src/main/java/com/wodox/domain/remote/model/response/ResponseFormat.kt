package com.starnest.domain.remote.model.response

import java.io.Serializable


data class ResponseFormat(
    var jsonSchema: JsonSchema,
    var type: String? = "json_schema"
) : Serializable {
    companion object {
        val grammarResponseFormat = ResponseFormat(
            jsonSchema = JsonSchema(
                name = "grammar",
                schema = Schema(
                    properties = GrammarProperties(),
                    required = listOf("original", "improved", "explanation", "isCorrect")
                )
            )
        )

        val synonymResponseFormat = ResponseFormat(
            jsonSchema = JsonSchema(
                name = "synonyms",
                schema = Schema(
                    properties = SynonymsProperties(),
                    required = listOf("synonyms")
                )
            )
        )

        val translatorResponseFormat = ResponseFormat(
            jsonSchema = JsonSchema(
                name = "translator",
                schema = Schema(
                    properties = TranslatorProperties(),
                    required = listOf("result", "languageDetected", "languageDetectedCode")
                )
            )
        )

        val checkViolateResponseFormat = ResponseFormat(
            jsonSchema = JsonSchema(
                name = "check_violate",
                schema = Schema(
                    properties = CheckViolateAndTranslateProperties(),
                    required = listOf("is_correct", "translated")
                ),
                strict = null
            )
        )

        val getSuggestionResponseFormat = ResponseFormat(
            jsonSchema = JsonSchema(
                name = "get_suggestions",
                schema = Schema(
                    properties = SuggestionProperties(),
                    required = listOf("suggestions")
                ),
                strict = null
            )
        )

        val getStorytellingResponseFormat = ResponseFormat(
            jsonSchema = JsonSchema(
                name = "get_storytelling",
                schema = Schema(
                    properties = StorytellingProperties(),
                    required = listOf("title", "content")
                ),
                strict = null
            )
        )

        val summaryKeypointAndLanguageResponseFormat = ResponseFormat(
            jsonSchema = JsonSchema(
                name = "get_summary_keypoint_and_language",
                schema = Schema(
                    properties = SummaryKeypointLanguageProperties(),
                    required = listOf("summary", "keypoint", "languageCode")
                ),
                strict = null
            )
        )

        val quizResponseFormat = ResponseFormat(
            jsonSchema = JsonSchema(
                name = "quiz",
                schema = Schema(
                    properties = QuizProperties(),
                    required = listOf("questions")
                ),
                strict = null
            )
        )

    }
}

data class JsonSchema(
    var name: String,
    var schema: Schema,
    var strict: Boolean? = true
)

data class Schema(
    var additionalProperties: Boolean? = false,
    var properties: Properties,
    var required: List<String>,
    var type: String? = "object"
)

interface Properties

data class ChatSuggestionItems(
    var type: String? = "string"
)

data class ChatSuggestion(
    var items: ChatSuggestionItems = ChatSuggestionItems(),
    var type: String? = "array"
)

data class SuggestionProperties(
    var suggestions: ChatSuggestion = ChatSuggestion()
) : Properties

data class QuizAnswerItems(
    var type: String? = "string"
)

data class QuizAnswerProperties(
    var items: QuizAnswerItems = QuizAnswerItems(),
    var type: String? = "array"
): Properties

data class QuizQuestionItems(
    var question: Type? = Type(),
    var answers: QuizAnswerProperties? = QuizAnswerProperties(),
    var correctAnswer: Type? = Type(),
): Properties

data class QuizQuestionProperties(
    var items: QuizQuestionItems = QuizQuestionItems(),
    var type: String? = "array"
): Properties

data class QuizProperties(
    var questions: QuizQuestionProperties = QuizQuestionProperties()
) : Properties

data class SummaryKeypointLanguageProperties(
    var summary: Type? = Type(),
    var keypoint: Type? = Type(),
    var languageCode: Type? = Type(),
) : Properties

data class StorytellingProperties(
    var title: Type? = Type(),
    var content: Type? = Type()
) : Properties

data class CheckViolateAndTranslateProperties(
    var isCorrect: Type? = Type(
        type = "boolean",
        description = "The final answer that sentences have any sexual words"
    ),
    var translated: Type? = Type(
        type = "string",
        description = "translate prompt translate to english"
    )
) : Properties

data class CheckViolateAndTranslate(
    var isViolate: Boolean,
    var translated: String
)

data class SynonymsProperties(
    var synonyms: Synonyms = Synonyms()
) : Properties

data class AutoGrammarProperties(
    var improved: Type? = Type(),
    var isCorrect: Type? = Type(
        type = "boolean",
        description = "The final answer that sentences is correct grammar."
    )
) : Properties

data class TranslatorProperties(
    var result: Type? = Type(),
    var languageDetected: Type? = Type(
        type = "string",
        description = "The language detected from source language."
    ),
    var languageDetectedCode: Type? = Type(
        type = "string",
        description = "The language code detected from source language."
    )
) : Properties

data class GrammarProperties(
    var explanation: Type? = Type(),
    var improved: Type? = Type(),
    var original: Type? = Type(),
    var isCorrect: Type? = Type(
        type = "boolean",
        description = "The final answer that sentences is correct grammar."
    )
) : Properties

data class Synonyms(
    var items: Items = Items(),
    var type: String? = "array"
)

data class Items(
    var additionalProperties: Boolean? = false,
    var properties: SynonymProperties? = SynonymProperties(),
    var required: List<String> = listOf("example", "explain", "word"),
    var type: String? = "object"
)

data class SynonymProperties(
    var example: Type? = Type(),
    var explain: Type? = Type(),
    var word: Type? = Type()
)

data class Type(
    var type: String? = "string",
    var description: String? = null
)
