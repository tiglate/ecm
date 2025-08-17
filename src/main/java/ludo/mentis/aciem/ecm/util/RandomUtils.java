package ludo.mentis.aciem.ecm.util;

import java.time.LocalDate;
import java.util.List;

public interface RandomUtils {

    <T> List<T> createRandomSublist(List<T> list, int n);

    <T extends Enum<?>> T pickRandomEnumValue(Class<T> enumClass);

    boolean pickRandomBoolean();

    LocalDate getRandomDate(LocalDate start, LocalDate end);

    int getRandomNumberInRange(int origin, int bound);
}