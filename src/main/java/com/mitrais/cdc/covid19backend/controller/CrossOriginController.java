/**
 * <h1>CrossOriginController</h1>
 * Class to set CORS for certain url
 *
 * @author Syarif Hidayat
 * @version 1.0
 * @since 2019-08-20
 * */


package com.mitrais.cdc.covid19backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * corsurl will contain url that included in CORS configuration
 *
 */
@CrossOrigin(origins={"*"})
public class CrossOriginController {
}
