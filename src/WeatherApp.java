import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// retorna os dados da API
// A interface irá mostrar os dados ao usuário
public class WeatherApp {
    // Faz um fetch dos dados do clima de acordo com a localização
    public static JSONObject getWeatherData(String locationName){
        // Puxa as cordenadas da localização de acordo com a API de geolocalização
        JSONArray locationData = getLocationData(locationName);

        // Puxa a latitude longitude dos dados 
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // Cria a request para API usando as cordenadas 
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FLos_Angeles";

        try{
            // Chama a API e resposta
            HttpURLConnection conn = fetchApiResponse(urlString);

            // Checa o status da resposta
            // retorna 200 caso tenha resposta
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // Armazena o resultado em JSON
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                // Le e guarda dentro do constructor
                resultJson.append(scanner.nextLine());
            }

            // fecha o  scanner
            scanner.close();

            // fecha a conexão URL
            conn.disconnect();

            // Analisa os dados
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // Recupera os dados por hora
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            // Devemos retornar os dados de acordo com a hora atual
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // Puxa a temperatura
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // Puxa o clima
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // Puxa humidade
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // Puxa a velocidade do tempo
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            // Constrói o JSON do clima como objeto que iremos utilizar para acessar os dddos pela interface
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // Retornas as coordenadas geográficas de acordo com a localização passada
    public static JSONArray getLocationData(String locationName){
        // substitua qualquer espaço em branco no nome do local por + para aderir ao formato de solicitação da API
        locationName = locationName.replaceAll(" ", "+");

        // Constrói a URL da API de acordo com os paramêtros da localização
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try{
            // chama e API e puxa uma resposta
            HttpURLConnection conn = fetchApiResponse(urlString);

            // checa o status da resposta 
            // 200 é sucesso
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                // Armazena o resultado da API
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                // Le a armazena o resultado do JSON no constructor
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                // fecha o scanner
                scanner.close();

                // fecha a conexão da URL
                conn.disconnect();

                // Transforma uma JSON String em uma JSON Object
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // Obtem uma lista de dados da localização que a API gerou de acordo com a localidade inserida
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        // Caso não ache a localização
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            // Tentiva de criar conexão
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //Seta o método para GET
            conn.setRequestMethod("GET");

            // Conecta com a API
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }

        // Caso não faça conexão
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        // Percorre a lista de horário em busca do nosso horário atual
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                // returna o index
                return i;
            }
        }

        return 0;
    }

    private static String getCurrentTime(){
        // Puxa a atual data e hora
        LocalDateTime currentDateTime = LocalDateTime.now();

        // muda o formato da data para 2023-09-02T00:00 (É a maneira que a API lê)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // forma e mostrar o dia e hora atual
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    // converte o código do clima em algo mais legível
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            // limpo
            weatherCondition = "Clear";
        }else if(weathercode > 0L && weathercode <= 3L){
            // nublado
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <= 67L)
                    || (weathercode >= 80L && weathercode <= 99L)){
            // chuva
            weatherCondition = "Rain";
        }else if(weathercode >= 71L && weathercode <= 77L){
            // neve
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}







