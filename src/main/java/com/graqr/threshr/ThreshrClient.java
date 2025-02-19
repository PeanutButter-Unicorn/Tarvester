package com.graqr.threshr;

import com.graqr.threshr.model.queryparam.TargetStore;
import com.graqr.threshr.model.queryparam.Tcin;
import com.graqr.threshr.model.redsky.product.pdp.client.PdpClientRoot;
import com.graqr.threshr.model.redsky.product.plp.search.PlpSearchRoot;
import com.graqr.threshr.model.redsky.product.summary.ProductSummaryRoot;
import com.graqr.threshr.model.redsky.store.location.StoreLocationRoot;
import com.graqr.threshr.model.redsky.store.nearby.NearbyStoreRoot;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Retryable;

import static io.micronaut.http.HttpHeaders.ACCEPT;

/**
 * This is a Micronaut HttpClient which consumes the target corporation's api.
 *
 * @author jonathan zollinger
 * @since 0.0.11
 */
@Client(id = "redsky-api")
@Header(name = ACCEPT, value = "text/html," +
        "application/xhtml+xml," +
        "application/xml;q=0.9," +
        "image/avif,image/webp," +
        "image/png," +
        "image/svg+xml," +
        "*/*;q=0.8")
interface ThreshrClient {

    /**
     * Queries target's product summaries from of the given tcins at a given target store.
     * A product summary does not include pricing.
     *
     * @param tcins       tcin ID's for products to query. see {@link Tcin#setTcins(String...)}
     * @param targetStore store from which the product summaries are to be queried.
     */
    @Retryable
    @Get("/product_summary_with_fulfillment_v1" +
            "?key=${threshr.key}" +
            "{&tcins*}" +
            "{&targetStore*}" +
            "&CHANNEL=${threshr.channel}")
    HttpResponse<ProductSummaryRoot> getProductSummary(
            TargetStore targetStore,
            Tcin tcins);

    /**
     * Get the pdp for the product with the given tcin from the given store.
     *
     * @param tcin           tcin ID's for products to query
     * @param pricingStoreId I really don't know why this second iteration of storeId is needed.
     * @param storeId        store from which the product summaries are to be queried.
     */
    @Retryable
    @Get("/pdp_client_v1" +
            "?key=${threshr.key}" +
            "{&tcin}" +
            "{&pricingStoreId}" +
            "{&storeId}")
    HttpResponse<PdpClientRoot> getProductDetails(
            @QueryValue("pricing_store_id") String pricingStoreId,
            @QueryValue("store_id") String storeId,
            String tcin);

    /**
     * Queries the 'plp_search_vs' endpoint for a given category at a given store. plp stands for product listing page.
     *
     * @param pricingStoreId store from which the product listings are to be queried.
     * @param visitorId      id for the visitor. This could be meaningless, but can't be null.
     * @param category       Target's internal category id.
     * @param page           Seems to be the category value prepended with "/c/"
     * @param channel        communication through which this api is being called. it's always 'WEB'
     * @return HttpResponse object containing ProductListings object
     */
    @Get("plp_search_v2" +
            "?key=${threshr.key}" +
            "{&category}" +
            "{&offset}" +
            "{&channel}" +
            "{&page}" +
            "{&pricingStoreId}" +
            "{&visitorId}")
    HttpResponse<PlpSearchRoot> getProductListings(
            @QueryValue("pricing_store_id") String pricingStoreId,
            @QueryValue("visitor_id") String visitorId,
            int offset,
            String category,
            String page,
            String channel);

    /**
     * returns target stores within a given distance from a location.
     *
     * @param limit  count of locations to return
     * @param within distance from place to include in results
     * @param place  either a zipcode or a city + state wrapped as a place object
     * @return TargetStoreRoot object in an HttpResponse object. the NearbyStores object can be found in a
     * TargetStoreRoot object.
     */
    @Retryable
    @Get("nearby_stores_v1" +
            "?key=${threshr.key}" +
            "{&limit}" +
            "{&within}" +
            "{&place}" +
            "&CHANNEL=${threshr.channel}")
    HttpResponse<NearbyStoreRoot> getNearbyStores(int limit, int within, String place);

    /**
     * Get Store Information (ie store hours) for a specific Target Store
     *
     * @return Store object, generally with more information about the store than other store endpoints.
     */
    @Get("store_location_v1" +
            "?key=${threshr.key}" +
            "{&store}" +
            "{&channel}" +
            "{&page}")
    HttpResponse<StoreLocationRoot> getStore(@QueryValue("store_id") String store, String channel, String page);

}