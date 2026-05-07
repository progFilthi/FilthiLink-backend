package com.product.filthilink.services.links;

public class LinkService {

    /*
    * Get original url -> https://dirtybucket.store/beats/uuid_chars
    * get custom_alias: db_store -> if not we default to domain name.
    * generate random chars: az-AZ-09
    * pick a shortcode of 6 - 8 chars - within - random char set: az-AZ-0-9: abz132
    * https://dirtybucket.store/beats/uuid_chars - original url
    * redirect - 302 status code - so we can track analytics
    * shortUrl -> https://db_store/abz132
    * check db if shortUrl exists ? redirect :
    *
    * */
}
