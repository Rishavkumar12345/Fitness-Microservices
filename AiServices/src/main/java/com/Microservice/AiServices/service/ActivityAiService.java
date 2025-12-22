package com.Microservice.AiServices.service;

import com.Microservice.AiServices.model.Activity;
import com.Microservice.AiServices.model.Recommendation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String promat=createPromptForActivity(activity);
        String aiResponse=geminiService.getAnswer((promat));
        log.info("Response from Ai for Recommendation: {}",aiResponse);

        return processAiResonse(activity,aiResponse);

    }

    public Recommendation processAiResonse(Activity activity, String aiResponse){

        try{
            ObjectMapper mapper=new ObjectMapper();

            JsonNode rootnode=mapper.readTree(aiResponse);

            JsonNode textnode=rootnode.path("candidates")
                                        .get(0)
                                        .path("content")
                                        .path("parts")
                                        .get(0)
                                        .path("text");

            String jsoncontent=textnode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();

            log.info("Parsed Ai Response: {} "+jsoncontent);

            JsonNode analysisJson=mapper.readTree(jsoncontent);
            JsonNode analysisNode=analysisJson.path("analysis");

            StringBuilder fullAnaLysis=new StringBuilder();

            addAnalysisSection(fullAnaLysis,analysisNode,"overall","Overall");
            addAnalysisSection(fullAnaLysis,analysisNode,"pace","Pace");
            addAnalysisSection(fullAnaLysis,analysisNode,"hearRate","HeartRate");
            addAnalysisSection(fullAnaLysis,analysisNode,"caloriesBurned","CaloriesBurned");

            List<String>improvement=addImprovementSection(analysisJson.path("improvements"));
            List<String>suggestions=addSuggestionsSection(analysisJson.path("suggestions"));
            List<String>safety=addSafetySection(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnaLysis.toString().trim())
                    .improvements(improvement)
                    .safety(safety)
                    .suggestions(suggestions)
                    .createdAt(LocalDateTime.now())
                    .build();

        }catch (Exception e){
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> addSafetySection(JsonNode safety) {

        List<String>safetySection=new ArrayList<>();

        if(safety.isArray()){
            safety.forEach(item->safetySection.add(item.asText()));
        }

        return safetySection.isEmpty() ?
                Collections.singletonList("No specific safety provided"):
                safetySection;
    }

    private List<String> addSuggestionsSection(JsonNode suggestions) {

        List<String>suggestionSection=new ArrayList<>();

        if(suggestions.isArray()){
            suggestions.forEach(items-> {
                String area=suggestions.path("workout").asText();
                String detail=suggestions.path("description").asText();
                suggestionSection.add(String.format("%s: %s", area, detail));
            });
        }

        return suggestionSection.isEmpty() ?
                Collections.singletonList("No specific suggestions provided") :
                suggestionSection;
    }

    private List<String> addImprovementSection(JsonNode improvements) {

        List<String>improvementSection=new ArrayList<>();

        if(improvements.isArray()){
            improvements.forEach(items-> {
                String area=improvements.path("area").asText();
                String detail=improvements.path("recommendation").asText();
                improvementSection.add(String.format("%s: %s", area, detail));
            });
        }

        return improvementSection.isEmpty() ?
                Collections.singletonList("No specific improvements provided") :
                improvementSection;
    }

    private void addAnalysisSection(StringBuilder fullAnaLysis, JsonNode analysisNode, String key, String prefix) {

        if(!analysisNode.path(key).isMissingNode()){
            fullAnaLysis.append(prefix).append(analysisNode.path(key).asText()).append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
