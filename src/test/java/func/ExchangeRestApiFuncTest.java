package func;

import com.exchange.ExchangeApplication;
import com.exchange.cache.CurrencyCache;
import com.exchange.dao.CurrencyRepository;
import com.exchange.dao.RateRepository;
import com.exchange.models.Currency;
import com.exchange.service.ExchangeRatesService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ExchangeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExchangeRestApiFuncTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ExchangeRatesService exchangeRatesService;

    @Autowired
    CurrencyCache currencyCache;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    RateRepository rateRepository;

    String baseUrl;

    @PostConstruct
    public void init() {
        this.baseUrl = "http://localhost:" + port + "/api";

    }

    @AfterEach
    public void tearDown() {
        currencyCache.invalidateAll();
        currencyRepository.deleteAll();
        rateRepository.deleteAll();
    }

    @Test
    public void shouldReturnEmptyCurrencies() {
        assertThat(getCurrencies()).isEmpty();
    }

    @Test
    public void shouldReturnNotEmptyCurrencies() {
        String usd = "USD", euro = "EUR";

        exchangeRatesService.addCurrencies(List.of(usd, euro));
        List<String> currencies = getCurrencies();
        assertThat(currencies).hasSize(2);
        assertThat(currencies.contains(usd)).isTrue();
        assertThat(currencies.contains(euro)).isTrue();
    }

    @Test
    public void shouldAddCurrencies() {
        String usd = "USD", euro = "EUR";

        addCurrencies(List.of(usd, euro));

        List<String> currencies = getCurrencies();

        assertThat(currencies).hasSize(2);
        assertThat(currencies.contains(usd)).isTrue();
        assertThat(currencies.contains(euro)).isTrue();
    }

    @Test
    public void shouldReturnCurrencyRates() {
        String euro = "EUR";

        exchangeRatesService.addCurrencies(List.of(euro));
        List<String> currencies = getCurrencies();
        assertThat(currencies).hasSize(1);
        assertThat(currencies.contains(euro)).isTrue();

        Currency currency = getCurrency(euro);
        assertThat(currency).isNotNull();
        assertThat(currency.getCurrencyCode()).isEqualTo(euro);
        assertThat(currency.getRates()).isNotNull();
        assertThat(currency.getRates()).isNotEmpty();
        assertThat(currency.getDate()).isNotNull();
        assertThat(currency.getTimestamp()).isNotNull();
        assertThat(currency.getTimestamp()).isNotNull();
    }


    public Currency getCurrency(String currencyCode) {
        return restTemplate.getForEntity(baseUrl + "/exchangeRate?baseCurrency=" + currencyCode, Currency.class).getBody();
    }

    public List<String> getCurrencies() {
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl + "/exchangeRate/currencies", List.class);
        return response.getBody();
    }

    public void addCurrencies(List<String> currencyCodes) {
        restTemplate.postForEntity(baseUrl + "/exchangeRate/add/currencies?currencies=" + String.join(",", currencyCodes), null, Void.class);
    }


}
