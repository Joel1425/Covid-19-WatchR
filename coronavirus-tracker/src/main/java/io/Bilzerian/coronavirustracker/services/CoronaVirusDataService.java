package io.Bilzerian.coronavirustracker.services;

import io.Bilzerian.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Service // after app starts up
public class CoronaVirusDataService {
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static String VIRUS_DATA_DEATHS = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv"; // new
    private static String VIRUS_DATA_RECOVERED = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
    private List<LocationStats> allStats= new ArrayList<>();

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct // after construct execute the method
    @Scheduled(cron = "* * 1 * * *")
    //Spring create a proxy to call this method within the specified frequency because somebody has to call it
    //schedules the run of a method on a regular basis
    public void fetchVirusData() throws IOException, InterruptedException {
        List <LocationStats> newStats=new ArrayList<>(); // because of concurrency reasons as a lot of people are accessing service. we dont want them to get errors.

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        HttpClient client = HttpClient.newHttpClient(); // creating a client
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build(); // Creating reuest using builder pattern
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString()); // take the body and return it as a string
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        HttpClient clientDeaths = HttpClient.newHttpClient(); // creating a client
        HttpRequest requestDeaths = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_DEATHS)).build(); // Creating reuest using builder pattern
        HttpResponse<String> httpResponseDeaths = clientDeaths.send(requestDeaths, HttpResponse.BodyHandlers.ofString()); // take the body and return it as a string
        StringReader csvBodyReaderDeaths = new StringReader(httpResponseDeaths.body());
        Iterable<CSVRecord> recordsDeaths = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReaderDeaths);

        HttpClient clientRecovered = HttpClient.newHttpClient(); // creating a client
        HttpRequest requestRecovered = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_RECOVERED)).build(); // Creating reuest using builder pattern
        HttpResponse<String> httpResponseRecovered = clientRecovered.send(requestRecovered, HttpResponse.BodyHandlers.ofString()); // take the body and return it as a string
        StringReader csvBodyReaderRecovered = new StringReader(httpResponseRecovered.body());
        Iterable<CSVRecord> recordsRecovered = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReaderRecovered);

        int i=0;
        for (CSVRecord record : records)
        {
            int x=i;
            LocationStats locationStat = new LocationStats("NA",0,0,0,0,0,0,0,0,0);
            locationStat.setState((record.get("Province/State").compareTo("")!=0)?(record.get("Province/State")):"NA");
            locationStat.setCountry(record.get("Country/Region"));
            String key=record.get("Country/Region")+record.get("Province/State");
            int latestCases=Integer.parseInt(record.get(record.size()-1));
            int prevDayCases=Integer.parseInt(record.get(record.size()-2));
            locationStat.setLatestTotal(latestCases);
            int diff=latestCases-prevDayCases;
            locationStat.setDiff((diff<0)?0:diff);
            newStats.add(locationStat);
            map.put(key,x);
            i++;
        }
        i=0;
        for (CSVRecord record : recordsDeaths)
        {
            int x=i;
            LocationStats locationStat = newStats.get(x);
            int latestCases=Integer.parseInt(record.get(record.size()-1));
            int prevDayCases=Integer.parseInt(record.get(record.size()-2));
            locationStat.setDeaths(latestCases);
            locationStat.setDeathsDiff(((latestCases-prevDayCases)>=0?(latestCases-prevDayCases):0));
            latestCases*=100;
            double rate= (latestCases/(((newStats.get(x).getLatestTotal()*1.0)<=0)?1:(newStats.get(x).getLatestTotal()*1.0)));
            rate=round(rate,2);
            locationStat.setMortalityRate(rate);
            i++;
        }

        for (CSVRecord record : recordsRecovered)
        {
            String key=record.get("Country/Region")+record.get("Province/State");
            int idx=-1;
            if (map.containsKey(key))
                idx=map.get(key);
            if (idx!=-1) {
                LocationStats locationStat = newStats.get(idx);
                int latestCases = Integer.parseInt(record.get(record.size() - 1));
                int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
                locationStat.setRecovered(latestCases);
                locationStat.setRecoveredDiff(((latestCases-prevDayCases)>=0?(latestCases-prevDayCases):0));
                latestCases *= 100;
                double rate = (latestCases / (((newStats.get(idx).getLatestTotal() * 1.0)<=0)?1:(newStats.get(idx).getLatestTotal() * 1.0)));
                rate=round(rate,2);
                locationStat.setRecoveryRate(rate);
                int active = newStats.get(idx).getLatestTotal()-newStats.get(idx).getDeaths()-newStats.get(idx).getRecovered();
                locationStat.setActiveCases(active);
            }
        }

        this.allStats=newStats;
    }
}
