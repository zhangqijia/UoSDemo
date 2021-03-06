package stage;

import static stage.model.DataStore.*;

import stage.model.*;
import stage.util.FormattedFileRead;
import stage.util.PrintUtil;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * Stage1 main handler
 *
 * @author ZQJ
 * @date 4/13/2020
 */
public class Stage1Handler implements Handler {

    public static final String DISTANCE_FROM_EARTH = "distanceFromEarth";
    public static final String MAGNITUDE = "magnitude";

    @Override
    public void execute(String[] args) {
        // read all files into corresponding lists
        this.readFilesToList(args);
        // get statistics info and save into instance field statisticsResult
        this.getSummaryStatistics();
        // printout answers
        System.out.println(this);
    }

    /**
     * Answer Q1~Q10 and save answers into statistics result.
     */
    public void getSummaryStatistics() {
        //Q1: how many planets are there?
        int planetCount = PLANET_LIST.size();
        STATISTICS_RESULT.add(String.valueOf(planetCount));
        //Q2: how many Messier objects are there?
        int messierCount = MESSIER_LIST.size();
        STATISTICS_RESULT.add(String.valueOf(messierCount));
        //Q3: how many starts are there?
        int starCount = STAR_LIST.size();
        STATISTICS_RESULT.add(String.valueOf(starCount));
        //sort all object lists in ascending order of distance
        STAR_LIST.sort(new DistanceComparator<>());
        MESSIER_LIST.sort(new DistanceComparator<>());
        PLANET_LIST.sort(new DistanceComparator<>());
        //Q4. Which object is nearest to the Earth?
        STATISTICS_RESULT.add(getNearestObjects());
        //Q5. Which object is furthest from the Earth?
        STATISTICS_RESULT.add(getFurthestObjects());
        //Q6. Which is the nearest star?
        STATISTICS_RESULT.add(getElementsWithSamePropertyFromList(STAR_LIST, DISTANCE_FROM_EARTH, true));
        // sort starList in ascending order of magnitude
        STAR_LIST.sort(Comparator.comparing(AstronomicalObject::getMagnitude));
        //Q7. Which is the brightest star?
        //the brighter an object is, the lower its magnitude
        STATISTICS_RESULT.add(getElementsWithSamePropertyFromList(STAR_LIST, MAGNITUDE, true));
        //Q8. Which is the faintest star?
        //the fainter an object is, the higher its magnitude
        STATISTICS_RESULT.add(getElementsWithSamePropertyFromList(STAR_LIST, MAGNITUDE, false));
        //save all constellations into map and record the number of their members
        Map<String, Integer> constellationMap = new HashMap<>(STAR_LIST.size() >>> 2);
        countMembersOfConstellation(constellationMap);
        //Q9. How many constellations are there?
        STATISTICS_RESULT.add(constellationMap.size() + "");
        //Q10. Which constellation has the largest number of members?
        String q10AnswerStr = getConstellationsWithMaxMembers(constellationMap);
        STATISTICS_RESULT.add(q10AnswerStr);
    }

    /**
     * save all constellations and the number of their members into map
     *
     * @param constellationMap the container
     */
    private void countMembersOfConstellation(Map<String, Integer> constellationMap) {
        STAR_LIST.forEach(star -> {
            Integer oldVal = constellationMap.get(star.getConstellation());
            if (oldVal == null) {
                oldVal = 0;
            }
            oldVal++;
            constellationMap.put(star.getConstellation(), oldVal);
        });
        MESSIER_LIST.forEach(messier -> {
            Integer oldVal = constellationMap.get(messier.getConstellation());
            if (oldVal == null) {
                oldVal = 0;
            }
            oldVal++;
            constellationMap.put(messier.getConstellation(), oldVal);
        });
    }

    /**
     * @return the furthest objects' name
     */
    private String getFurthestObjects() {
        StringBuilder q5Answer = new StringBuilder();
        // get the nearest object from different types of object list separately.
        BigDecimal maxStarDistance = STAR_LIST.get(STAR_LIST.size() - 1).getDistanceFromEarth();
        BigDecimal maxMessierDistance = MESSIER_LIST.get(MESSIER_LIST.size() - 1).getDistanceFromEarth();
        BigDecimal maxPlanetDistance = PLANET_LIST.get(PLANET_LIST.size() - 1).getDistanceFromEarth();
        // find the nearest distance among three types of objects
        BigDecimal maxDistance = maxStarDistance.max(maxPlanetDistance.max(maxMessierDistance));
        // check whether other objects have the same max distance exist.
        // Use compareTo() because equal() considers two BigDecimal objects equal only if they are equal in value and scale, 2.0 2.00
        if (maxDistance.compareTo(maxPlanetDistance) == 0) {
            q5Answer.append(getElementsWithSamePropertyFromList(PLANET_LIST, DISTANCE_FROM_EARTH, false)).append(",");
        }
        if (maxDistance.compareTo(maxStarDistance) == 0) {
            q5Answer.append(getElementsWithSamePropertyFromList(STAR_LIST, DISTANCE_FROM_EARTH, false)).append(",");
        }
        if (maxDistance.compareTo(maxMessierDistance) == 0) {
            q5Answer.append(getElementsWithSamePropertyFromList(MESSIER_LIST, DISTANCE_FROM_EARTH, false)).append(",");
        }
        return q5Answer.substring(0, q5Answer.length() - 1);
    }

    /**
     * get nearest to Earth objects
     *
     * @return result String consists of the objects' names
     */
    private String getNearestObjects() {
        StringBuilder q4Answer = new StringBuilder();
        // get the nearest object from different types of object list separately.
        BigDecimal minStarDistance = STAR_LIST.get(0).getDistanceFromEarth();
        BigDecimal minMessierDistance = MESSIER_LIST.get(0).getDistanceFromEarth();
        BigDecimal minPlanetDistance = PLANET_LIST.get(0).getDistanceFromEarth();
        // find the nearest distance among three types of objects
        BigDecimal minDistance = minStarDistance.min(minPlanetDistance.min(minMessierDistance));
        // check whether other objects have the same min distance exist.
        if (minDistance.compareTo(minPlanetDistance) == 0) {
            q4Answer.append(getElementsWithSamePropertyFromList(PLANET_LIST, DISTANCE_FROM_EARTH, true)).append(",");
        }
        if (minDistance.compareTo(minStarDistance) == 0) {
            q4Answer.append(getElementsWithSamePropertyFromList(STAR_LIST, DISTANCE_FROM_EARTH, true)).append(",");
        }
        if (minDistance.compareTo(minMessierDistance) == 0) {
            q4Answer.append(getElementsWithSamePropertyFromList(MESSIER_LIST, DISTANCE_FROM_EARTH, true)).append(",");
        }
        return q4Answer.substring(0, q4Answer.length() - 1);
    }

    /**
     * get constellations which have the most members
     *
     * @param map the map of Constellations' statistics information
     * @return formatted answer of Q10
     */
    private String getConstellationsWithMaxMembers(Map<String, Integer> map) {
        ArrayList<String> maxMemberConstellations = new ArrayList<>();
        int max = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            // find a bigger number, clear the List and update max
            if (max < entry.getValue()) {
                max = entry.getValue();
                maxMemberConstellations.clear();
                maxMemberConstellations.add(entry.getKey());
            } else if (max == entry.getValue()) {
                maxMemberConstellations.add(entry.getKey());
            }
        }
        StringBuilder q10Answer = new StringBuilder();
        for (String str : maxMemberConstellations) {
            q10Answer.append(str).append(",");
        }
        return q10Answer.substring(0, q10Answer.length() - 1);
    }


    /**
     * get elements with the same property value from the head or the end of ascending sorted list;
     *
     * @param list         ascending sorted list
     * @param propertyName the property name
     * @param fromStart    if true, this method get elements from start, otherwise from end
     * @return String consists of names or CatalogueNumbers of elements which have the same value for assigned property
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T extends AstronomicalObject> String getElementsWithSamePropertyFromList(List<T> list, String propertyName, boolean fromStart) {
        List<T> answerList = new ArrayList<>();
        int startIndex;
        // if from the head, use the first element's property value as criterion, otherwise, use the last
        if (fromStart) {
            startIndex = 0;
        } else {
            startIndex = list.size() - 1;
        }
        T t = list.get(startIndex);
        answerList.add(t);
        PropertyDescriptor descriptor = null;
        Method readMethod = null;
        Comparable extremeValue = null;
        T other;
        try {
            //get the read method of specific property
            descriptor = new PropertyDescriptor(propertyName, t.getClass());
            readMethod = descriptor.getReadMethod();
            // get the min/max value of this property (first/last element's property value)
            extremeValue = (Comparable) readMethod.invoke(t);
            // if other objects has the same property, add to answer list.
            if (fromStart) {
                for (int i = 1; i < list.size(); i++) {
                    other = list.get(i);
                    Object property = readMethod.invoke(other);
                    if (extremeValue.compareTo(property) == 0) {
                        answerList.add(other);
                    } else {
                        // find different value, stop loop;
                        break;
                    }
                }
            } else {
                for (int i = list.size() - 2; i >= 0; i--) {
                    other = list.get(i);
                    Object property = readMethod.invoke(other);
                    if (extremeValue.compareTo(property) == 0) {
                        answerList.add(other);
                    } else {
                        break;
                    }
                }
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            PrintUtil.printExceptionAndExit(e, "failed to compare the value of:" + propertyName);
        } catch (ClassCastException e) {
            PrintUtil.printExceptionAndExit(e, "this property cannot be compared:" + propertyName);
        }
        // format answerList to String
        StringBuilder stringBuilder = new StringBuilder();
        for (T starCandidate : answerList) {
            stringBuilder.append(starCandidate.getCatalogueNumberOrName()).append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    /**
     * read file into fields
     *
     * @param fileNames filename's array
     */
    private void readFilesToList(String[] fileNames) {
        if (fileNames.length < 3) {
            PrintUtil.printExceptionAndExit(new Exception("ArgumentsException"), "you should give 3 formatted files");
        }
        // create FileReaderTool instances and read files into different lists according to astronomical type.
        FormattedFileRead<Star> starFileRead = new FormattedFileRead<>(fileNames[0]);
        FormattedFileRead<Messier> messierFileRead = new FormattedFileRead<>(fileNames[1]);
        FormattedFileRead<Planet> planetFileRead = new FormattedFileRead<>(fileNames[2]);
        starFileRead.readFileToList(STAR_LIST, Star.class);
        messierFileRead.readFileToList(MESSIER_LIST, Messier.class);
        planetFileRead.readFileToList(PLANET_LIST, Planet.class);
    }

    /**
     * the Distance from Earth comparator for AstronomicalObject
     *
     * @param <T>
     */
    static class DistanceComparator<T extends AstronomicalObject> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            return o1.getDistanceFromEarth().compareTo(o2.getDistanceFromEarth());
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < STATISTICS_RESULT.size(); i++) {
            stringBuilder.append("Q")
                    .append(i + 1)
                    .append(". ")
                    .append(STATISTICS_RESULT.get(i))
                    .append(" ");
        }
        return stringBuilder.toString();
    }
}