package com.np.onboarding.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.github.vanroy.springdata.jest.JestElasticsearchTemplate
import com.github.vanroy.springdata.jest.mapper.DefaultJestResultsMapper
import io.searchbox.client.JestClient
import java.io.IOException
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.EntityMapper
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext
import org.springframework.data.mapping.MappingException

@Configuration
@EnableConfigurationProperties(ElasticsearchProperties::class)
class ElasticsearchConfiguration(private val mapper: ObjectMapper) {

    @Bean
    fun getEntityMapper() = CustomEntityMapper(mapper)

    @Bean
    @Primary
    fun elasticsearchTemplate(
        jestClient: JestClient,
        elasticsearchConverter: ElasticsearchConverter,
        simpleElasticsearchMappingContext: SimpleElasticsearchMappingContext,
        mapper: EntityMapper
    ): ElasticsearchOperations {
        return JestElasticsearchTemplate(
            jestClient,
            elasticsearchConverter,
            DefaultJestResultsMapper(simpleElasticsearchMappingContext, mapper))
    }

    inner class CustomEntityMapper(private val objectMapper: ObjectMapper) : EntityMapper {

        init {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false)
            objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
        }

        @Throws(IOException::class)
        override fun mapToString(`object`: Any) = objectMapper.writeValueAsString(`object`)

        @Throws(IOException::class)
        override fun <T> mapToObject(source: String, clazz: Class<T>) = objectMapper.readValue(source, clazz)

        override fun mapObject(source: Any): Map<String, Any> {
            try {
                return objectMapper.readValue(mapToString(source), object : TypeReference<HashMap<String, Any>>() {})
            } catch (e: IOException) {
                throw MappingException(e.message, e)
            }
        }

        override fun <T> readObject(source: Map<String, Any>, targetType: Class<T>): T {
            try {
                return mapToObject(mapToString(source), targetType)
            } catch (e: IOException) {
                throw MappingException(e.message, e)
            }
        }
    }
}
