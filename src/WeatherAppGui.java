import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui(){
        // Setar a Interface e o título
        super("Weather App");

        // configura a interface para encerrar o processo do programa assim que ele for fechado
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // tamanho da interface (em pixels)
        setSize(450, 650);

        // carrega a interface no meio do centro da tela
        setLocationRelativeTo(null);

        // Permite editar o layout manualmente
        setLayout(null);

        // mantém o tamanho da interface
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents(){
        // campo de pesquisa
        JTextField searchTextField = new JTextField();

        // define a localização e tamanho do componente 
        searchTextField.setBounds(15, 15, 351, 45);

        // seta o tamanho e estilo da fonte
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        // imagem do clima
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // texto de temperatura
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // centraliza o texto
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // descrição do clima atual
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // imagem de humidade
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // texto da hmuidade
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //  imagem da velocidade do vento
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        // texto da velocidade do vento
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        // botão de buscar
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        // muda o cursor para uma mãozinha quando estiver sobre o botão
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // pega a localização inserida pelo usuário
                String userInput = searchTextField.getText();

                // valida o input e remove espaços em branco para assegurar non-empty text
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                // pega os dados do clima
                weatherData = WeatherApp.getWeatherData(userInput);

                // atualiza a interface

                // atualiza a imagem de acordo com a condição do clima
                String weatherCondition = (String) weatherData.get("weather_condition");

                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.pngImage"));
                        break;
                }

                // atualiza o texto da temperatura
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                // atualiza o texto da condição do clima
                weatherConditionDesc.setText(weatherCondition);

                // atualiza o texto da humidade
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // atualiza o texto da velocidade do vento
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);
    }

    // usados para criar imagens nos componentes da interface
    private ImageIcon loadImage(String resourcePath){
        try{

            // Le a imagem de acordo com o caminho apresentado
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // retorna um ícone-imagem para que o componente da interface possa renderizá-lo
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}









