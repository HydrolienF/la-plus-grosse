package fr.formiko.laplusgrosse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.Map;

// "1";"Gretchen";"Barrows-Kassulke";"Gretchen.Barrows-Kassulke@yahoo.com";"Jamaica";"3954"
// Just reading files in Lapinou take ~300 ms with parallel() and ~400 ms without parallel()
public class App {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try {
            Map<String, Map<Integer, Long>> result =
                Files.lines(Path.of("LargeDataSet.csv"))
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
                    // // Collect a Map<Person, Long> where the key is the Person and the value is the number of people with that country and postal code
                    // .collect(
                    //     Collectors.groupingBy(
                    //         person -> person,
                    //         Collectors.counting()
                    //     )
                    // )
                    // // Person[country=Burkina Faso, postalCode=4862] : 1
                    // // Collect a Map<String, Map<Integer, Integer>> where the key is the country and the value is a Map<Integer, Integer> where the key is the postal code and the value is the number of people with that country and postal code
                    // .collect(
                    //     Collectors.groupingBy(
                    //         person -> person.country(),
                    //         Collectors.groupingBy(
                    //             person -> person.postalCode(),
                    //             Collectors.summingInt(person -> 1)
                    //         )
                    //     )
                    // )
                    .collect(
                        Collectors.groupingBy(
                            person -> person.country(),
                            Collectors.groupingBy(
                                person -> person.postalCode(),
                                Collectors.counting()
                            )
                        )
                    );
                    // .entrySet().stream()
                    // .sorted((entry1, entry2) -> Long.compare(entry1.getValue().values().stream().mapToLong(Long::longValue).sum(), entry2.getValue().values().stream().mapToLong(Long::longValue).sum()))
                    // .limit(10)
                    // .forEach(System.out::println);


                    // .sort((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                    // print all
                    // .forEach((person, count) -> System.out.println(person + " : " + count));
                    // .forEach((country, postalCodeMap) -> {
                    //     System.out.println(country + " : ");
                    //     postalCodeMap.forEach((postalCode, count) -> {
                    //         System.out.println("\t" + postalCode + " : " + count);
                    //     });
                    // });



                    // // Create a Data structure to store Country, PostalCode, Number of people with that country and postal code
                    // .map(person -> person.postalCode())
                    // .max(Integer::compare)
                    // .ifPresent(System.out::println);
                    // .count();
                    //.findFirst().ifPresent(System.out::println);
            
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Temps de traitement : " + (System.currentTimeMillis() - start) + " ms");
    }


    // class CountryCollector extends Collector<Map<Person, Long>, Object, Map<String, Map<Integer, Integer>>> {

    // }
}
