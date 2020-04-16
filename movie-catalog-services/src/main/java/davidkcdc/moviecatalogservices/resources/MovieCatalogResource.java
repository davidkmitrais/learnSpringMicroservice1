package davidkcdc.moviecatalogservices.resources;

import davidkcdc.moviecatalogservices.models.CatalogItem;
import davidkcdc.moviecatalogservices.models.Movie;
import davidkcdc.moviecatalogservices.models.Rating;
import davidkcdc.moviecatalogservices.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog (@PathVariable("userId") String userId){

        UserRating ratings = restTemplate.getForObject("http://rating-data-service/ratingsdata/users/" + userId, UserRating.class);

        return ratings.getUserRating().stream().map(rating -> {
                //Foreach movieId, call movie info service and get details
                Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

                /*
                Movie movie = webClientBuilder.build()
                        .get()
                        .uri("http://localhost:8082/movies/" + rating.getMovieId())
                        .retrieve()
                        .bodyToMono(Movie.class)
                        .block();
                 */
                //put them all together
                return new CatalogItem( movie.getName(), "film Transformer", rating.getRating());
            })
        .collect(Collectors.toList());
    }
}
