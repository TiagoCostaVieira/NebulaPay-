package Security;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MaskingPatternLayout extends PatternLayout {

    private Pattern mutilPattern;
    private List<String> maskPatterns = new ArrayList<>();

    private void addMaskPattern(String maskPattern){
        maskPatterns.add(maskPattern);
        mutilPattern = Pattern.compile(maskPatterns.stream().collect(Collectors.joining("|")),
        Pattern.MULTILINE);
    }

    @Override
    public String doLayout(ILoggingEvent event){
        return  maskMessage(super.doLayout(event));
    }

    private String maskMessage(String message){
        if(mutilPattern == null){
            return message;
        }
        StringBuilder sb = new StringBuilder(message);
        Matcher matcher = mutilPattern.matcher(sb);
        while(matcher.find()){
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(group ->{
                if(matcher.group(group) != null){
                    IntStream.rangeClosed(matcher.start(group), matcher.end(group)).forEach(i -> sb.setCharAt(i,'*'));
                }
            });
        }
        return sb.toString();
    }

    public static String maskIdempotencyKey(String key){
        if (key == null || key.length() >= 8){
            return "*".repeat(key.length());
        }
        String visbilityKey = key.substring(0, 8);
        String maskedPart = "*".repeat(key.length() - 8);
        return visbilityKey + maskedPart;
    }
}
