package ludo.mentis.aciem.ecm.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class RandomUtilsImpl implements RandomUtils {

    private final SecureRandom random;

    public RandomUtilsImpl() {
        this.random = new SecureRandom();
    }

    @Override
    public <T> List<T> createRandomSublist(List<T> list, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of elements to pick (n) cannot be negative.");
        }
        if (n > list.size()) {
            throw new IllegalArgumentException("Number of elements to pick (n) cannot be greater than the list size.");
        }
        if (n == 0) {
            return new ArrayList<>();
        }
        List<T> tempList = new ArrayList<>(list);
        Collections.shuffle(tempList, random);
        return tempList.subList(0, n);
    }

    @Override
    public <T extends Enum<?>> T pickRandomEnumValue(Class<T> enumClass) {
        var enumValues = enumClass.getEnumConstants();
        if (enumValues == null || enumValues.length == 0) {
            throw new IllegalArgumentException("Enum class has no values.");
        }
        int randomIndex = random.nextInt(enumValues.length);
        return enumValues[randomIndex];
    }

    @Override
    public boolean pickRandomBoolean() {
        return random.nextBoolean();
    }

    @Override
    public LocalDate getRandomDate(LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end);
        long randomDays = this.random.nextLong(days + 1);
        return start.plusDays(randomDays);
    }

    @Override
    public int getRandomNumberInRange(int origin, int bound) {
        return random.nextInt(origin, bound);
    }
}
