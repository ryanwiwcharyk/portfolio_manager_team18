package com.portfoliomanager.team18;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.ObjectInputFilter;

@SpringBootApplication
public class PortfolioManagerTeam18Application {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		Config cfg = Config.builder()
				.key(dotenv.get("API_KEY"))
				.timeOut(10)
				.build();
		AlphaVantage.api().init(cfg);
		SpringApplication.run(PortfolioManagerTeam18Application.class, args);
	}

}