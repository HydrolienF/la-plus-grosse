package fr.formiko.laplusgrosse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.List;

// "1";"Gretchen";"Barrows-Kassulke";"Gretchen.Barrows-Kassulke@yahoo.com";"Jamaica";"3954"
// Just reading files in Lapinou take ~60 ms with parallel() and ~80 ms without parallel()
public class App {
    public static void main(String[] args) {
        // run once
        long start = System.currentTimeMillis();
        List<String> result = getTenMostUsedPostalCode(getCountryData());

        for (int i = 0; i < result.size(); i++) {
            System.out.println(i + 1 + ". " + result.get(i));
        }
        System.out.println("\nTemps de traitement : " + (System.currentTimeMillis() - start) + " ms");

        // run x times to get an average, min & max run time
        // int x = 100;
        // long min = Long.MAX_VALUE;
        // long max = Long.MIN_VALUE;
        // long total = 0;
        // for (int i = 0; i < x; i++) {
        //     long start = System.currentTimeMillis();
        //     getTenMostUsedPostalCode(getCountryData());
        //     long time = System.currentTimeMillis() - start;
        //     total += time;
        //     min = Math.min(min, time);
        //     max = Math.max(max, time);
        //     // System.out.println(i + 1 + ". " + time + " ms");
        //     try {
        //         Thread.sleep(100);
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        // }
        // System.out.println("\nTemps de traitement moyen : " + total / x + " ms");
        // System.out.println("Temps de traitement minimal : " + min + " ms");
        // System.out.println("Temps de traitement maximal : " + max + " ms");
    }

    private static Map<String, Map<Integer, Long>> getCountryData() {
        try {
            return Files.lines(Path.of("LargeDataSet.csv"))
                // Files.lines(Path.of("SmallDataSet.csv"))
                    .parallel()
                    .map(csvLine -> {
                        // La méthode simple, mais qui crée beaucoup de String inutiles.
                        // String[] t = s.split(";");
                        // return new Person(t[4].replace("\"",""), Integer.parseInt(t[5].replace("\"","")));

                        // La méthode plus 'sale', mais qui limite au maximum la création de String inutiles.
                        int lastSeparator = csvLine.lastIndexOf(";");
                        int secondLastSeparator = csvLine.lastIndexOf(";", lastSeparator - 1);

                        String country = csvLine.substring(secondLastSeparator + 2, lastSeparator - 1);
                        // String last = csvLine.substring(lastSeparator + 2, csvLine.length() - 1);
                        int postalCode = Integer.parseInt(csvLine,lastSeparator + 2 , csvLine.length() - 1,10);
                        
                        return new Person(country, postalCode);

                    })
                    .collect(
                        Collectors.groupingBy(
                            person -> person.country(),
                            Collectors.groupingBy(
                                person -> person.postalCode(),
                                Collectors.counting()
                            )
                        )
                    );
        }catch (IOException e) {
            e.printStackTrace();
            return Map.of();
        }
    }
    /**
     * 
     * @return the 10 most used postal code compared to the total number of people living in the country
     */
    private static List<String> getTenMostUsedPostalCode(Map<String, Map<Integer, Long>> contryData){
        // From Map<String, Map<Integer, Long>> to Map<Person, Float> where the float is a percentage of the total number of people leaving in that country & in that postal code
        return contryData.entrySet().stream()
                .parallel()
                .map(
                    countryEntry -> {
                        long total = countryEntry.getValue().values().stream().mapToLong(Long::longValue).sum();
                        return countryEntry.getValue().entrySet().stream()
                            .map(
                                postalCodeEntry -> {
                                    float percentage = (float) postalCodeEntry.getValue() / total * 100;
                                    return Map.entry(new Person(countryEntry.getKey(), postalCodeEntry.getKey()), percentage);
                                }
                            );
                    }
                )
                .flatMap(entry -> entry)
                .sorted((entry1, entry2) -> Float.compare(entry2.getValue(), entry1.getValue()))
                .limit(10)
                .map(entry -> String.format("%s, %s, %.2f%%", entry.getKey().country(), entry.getKey().postalCode(), entry.getValue()))
                .toList();
    }

}
