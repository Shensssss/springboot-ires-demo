package tw.idv.shen.core.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ListToJsonConverter implements AttributeConverter<List<String>, String> {
    private static final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        try {
            return gson.toJson(list); // 轉成 JSON 陣列字串
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not convert list to JSON", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String json) {
        try {
            return gson.fromJson(json, new TypeToken<List<String>>() {}.getType()); // JSON 轉回 List<String>
        } catch (JsonSyntaxException e) {
            // 加上容錯處理：如果是錯誤格式（如 JSON 物件），則略過或轉為空 list
            System.err.println("JSON格式錯誤（不是陣列）：" + json);
            return new ArrayList<>();
        }
    }
}
