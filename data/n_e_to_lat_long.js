/**
 * Converts ‘this’ lat/lon coordinate to new coordinate system.
 *
 * @param   {LatLon.datum} toDatum - Datum this coordinate is to be converted to.
 * @returns {LatLon} This point converted to new datum.
 *
 * @example
 *     var pWGS84 = new LatLon(51.4778, -0.0016, LatLon.datum.WGS84);
 *     var pOSGB = pWGS84.convertDatum(LatLon.datum.OSGB36); // 51.4773°N, 000.0000°E
 */
LatLon.prototype.convertDatum = function(toDatum) {
    var oldLatLon = this;
    var transform = null;

    if (oldLatLon.datum == LatLon.datum.WGS84) {
        // converting from WGS 84
        transform = toDatum.transform;
    }
    if (toDatum == LatLon.datum.WGS84) {
        // converting to WGS 84; use inverse transform (don't overwrite original!)
        transform = [];
        for (var p=0; p<7; p++) transform[p] = -oldLatLon.datum.transform[p];
    }
    if (transform == null) {
        // neither this.datum nor toDatum are WGS84: convert this to WGS84 first
        oldLatLon = this.convertDatum(LatLon.datum.WGS84);
        transform = toDatum.transform;
    }

    var oldCartesian = oldLatLon.toCartesian();                // convert polar to cartesian...
    var newCartesian = oldCartesian.applyTransform(transform); // ...apply transform...
    var newLatLon = newCartesian.toLatLonE(toDatum);           // ...and convert cartesian to polar

    return newLatLon;
};


/**
 * Converts ‘this’ point from (geodetic) latitude/longitude coordinates to (geocentric) cartesian
 * (x/y/z) coordinates.
 *
 * @returns {Vector3d} Vector pointing to lat/lon point, with x, y, z in metres from earth centre.
 */
LatLon.prototype.toCartesian = function() {
    var phi = this.lat.toRadians(), lambda = this.lon.toRadians();
    var h = 0; // height above ellipsoid - not currently used
    var a = this.datum.ellipsoid.a, f = this.datum.ellipsoid.f;

    var sinphi = Math.sin(phi), cosphi = Math.cos(phi);
    var sinlambda = Math.sin(lambda), coslambda = Math.cos(lambda);

    var eSq = 2*f - f*f;                      // 1st eccentricity squared ≡ (a^2-b^2)/a^2
    var Nu = a / Math.sqrt(1 - eSq*sinphi*sinphi); // radius of curvature in prime vertical

    var x = (Nu+h) * cosphi * coslambda;
    var y = (Nu+h) * cosphi * sinlambda;
    var z = (Nu*(1-eSq)+h) * sinphi;

    var point = new Vector3d(x, y, z);

    return point;
};

/**
 * Converts Ordnance Survey grid reference easting/northing coordinate to latitude/longitude
 * (SW corner of grid square).
 *
 * Note formulation implemented here due to Thomas, Redfearn, etc is as published by OS, but is
 * inferior to Krüger as used by e.g. Karney 2011.
 *
 * @param   {OsGridRef}    gridref - Grid ref E/N to be converted to lat/long (SW corner of grid square).
 * @param   {LatLon.datum} [datum=WGS84] - Datum to convert grid reference into.
 * @returns {LatLon}       Latitude/longitude of supplied grid reference.
 *
 * @example
 *   var gridref = new OsGridRef(651409.903, 313177.270);
 *   var pWgs84 = OsGridRef.osGridToLatLon(gridref);                     // 52°39′28.723″N, 001°42′57.787″E
 *   // to obtain (historical) OSGB36 latitude/longitude point:
 *   var pOsgb = OsGridRef.osGridToLatLon(gridref, LatLon.datum.OSGB36); // 52°39′27.253″N, 001°43′04.518″E
 */
OsGridRef.osGridToLatLon = function(gridref, datum) {
    if (!(gridref instanceof OsGridRef)) throw new TypeError('gridref is not OsGridRef object');
    if (datum === undefined) datum = LatLon.datum.WGS84;

    var E = gridref.easting;
    var N = gridref.northing;

    var a = 6377563.396, b = 6356256.909;              // Airy 1830 major & minor semi-axes
    var F0 = 0.9996012717;                             // NatGrid scale factor on central meridian
    var phi0 = (49).toRadians(), lambda0 = (-2).toRadians();  // NatGrid true origin is 49°N,2°W
    var N0 = -100000, E0 = 400000;                     // northing & easting of true origin, metres
    var e2 = 1 - (b*b)/(a*a);                          // eccentricity squared
    var n = (a-b)/(a+b), n2 = n*n, n3 = n*n*n;

    var phi=phi0, M=0;
    do {
        phi = (N-N0-M)/(a*F0) + phi;

        var Ma = (1 + n + (5/4)*n2 + (5/4)*n3) * (phi-phi0);
        var Mb = (3*n + 3*n*n + (21/8)*n3) * Math.sin(phi-phi0) * Math.cos(phi+phi0);
        var Mc = ((15/8)*n2 + (15/8)*n3) * Math.sin(2*(phi-phi0)) * Math.cos(2*(phi+phi0));
        var Md = (35/24)*n3 * Math.sin(3*(phi-phi0)) * Math.cos(3*(phi+phi0));
        M = b * F0 * (Ma - Mb + Mc - Md);              // meridional arc

    } while (N-N0-M >= 0.00001);  // ie until < 0.01mm

    var cosphi = Math.cos(phi), sinphi = Math.sin(phi);
    var Nu = a*F0/Math.sqrt(1-e2*sinphi*sinphi);            // nu = transverse radius of curvature
    var rho = a*F0*(1-e2)/Math.pow(1-e2*sinphi*sinphi, 1.5); // rho = meridional radius of curvature
    var Eta2 = Nu/rho-1;                                    // eta = ?

    var tanphi = Math.tan(phi);
    var tan2phi = tanphi*tanphi, tan4phi = tan2phi*tan2phi, tan6phi = tan4phi*tan2phi;
    var secphi = 1/cosphi;
    var Nu3 = Nu*Nu*Nu, Nu5 = Nu3*Nu*Nu, Nu7 = Nu5*Nu*Nu;
    var VII = tanphi/(2*rho*Nu);
    var VIII = tanphi/(24*rho*Nu3)*(5+3*tan2phi+Eta2-9*tan2phi*Eta2);
    var IX = tanphi/(720*rho*Nu5)*(61+90*tan2phi+45*tan4phi);
    var X = secphi/Nu;
    var XI = secphi/(6*Nu3)*(Nu/rho+2*tan2phi);
    var XII = secphi/(120*Nu5)*(5+28*tan2phi+24*tan4phi);
    var XIIA = secphi/(5040*Nu7)*(61+662*tan2phi+1320*tan4phi+720*tan6phi);

    var dE = (E-E0), dE2 = dE*dE, dE3 = dE2*dE, dE4 = dE2*dE2, dE5 = dE3*dE2, dE6 = dE4*dE2, dE7 = dE5*dE2;
    phi = phi - VII*dE2 + VIII*dE4 - IX*dE6;
    var lambda = lambda0 + X*dE - XI*dE3 + XII*dE5 - XIIA*dE7;

    var point =  new LatLon(phi.toDegrees(), lambda.toDegrees(), LatLon.datum.OSGB36);
    if (datum != LatLon.datum.OSGB36) point = point.convertDatum(datum);

    return point;
};
