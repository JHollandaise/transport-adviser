def toRadians(angle):
    return (angle/180)*Math.pi

def ToDegrees(angle):
    return (angle*180)/Math.pi

def OsGridToLatLong(gridref):
    E = gridref[0]
    N = gridref[1]

    a, b = 6377563.396, 6356256.909                 # Airy 1830 major & minor semi-axes
    F0 = 0.9996012717                               # NatGrid scale factor on central meridian
    phi0, lambdaa0 = toRadians(49), toRadians(-2)    # NatGrid true origin is 49°N,2°W
    N0, E0 = -100000, 400000                        # northing & easting of true origin, metres
    e2 = 1 - (b*b)/(a*a)                            # eccentricity squared
    n = (a-b)/(a+b)                                 # n, n², n³
    n2, n3 = n*n, n*n*n

    phi=phi0
    M=0

    phi = (N-N0-M)/(a*F0) + phi

    Ma = (1 + n + (5/4)*n2 + (5/4)*n3) * (phi-phi0)
    Mb = (3*n + 3*n*n + (21/8)*n3) * Math.sin(phi-phi0) * Math.cos(phi+phi0)
    Mc = ((15/8)*n2 + (15/8)*n3) * Math.sin(2*(phi-phi0)) * Math.cos(2*(phi+phi0))
    Md = (35/24)*n3 * Math.sin(3*(phi-phi0)) * Math.cos(3*(phi+phi0))
    M = b * F0 * (Ma - Mb + Mc - Md)

    while N-N0-M >= 0.00001:
        phi = (N-N0-M)/(a*F0) + phi

        Ma = (1 + n + (5/4)*n2 + (5/4)*n3) * (phi-phi0)
        Mb = (3*n + 3*n*n + (21/8)*n3) * Math.sin(phi-phi0) * Math.cos(phi+phi0)
        Mc = ((15/8)*n2 + (15/8)*n3) * Math.sin(2*(phi-phi0)) * Math.cos(2*(phi+phi0))
        Md = (35/24)*n3 * Math.sin(3*(phi-phi0)) * Math.cos(3*(phi+phi0))
        M = b * F0 * (Ma - Mb + Mc - Md)

    cosphi = Math.cos(phi)
    sinphi = Math.sin(phi)

    nu = a*F0/Math.sqrt(1-e2*sinphi*sinphi)
    rho = a*F0*(1-e2)/Math.pow(1-e2*sinphi*sinphi, 1.5)
    eta2 = nu/rho-1;

    tanphi = Math.tan(phi)
    tan2phi = tanphi*tanphi
    tan4phi = tan2phi*tan2phi
    tan6phi = tan4phi*tan2phi

    secphi = 1/cosphi

    nu3 = nu*nu*nu
    nu5 = nu3*nu*nu
    nu7 = nu5*nu*nu

    VII = tanphi/(2*rho*nu)
    VIII = tanphi/(24*rho*nu3)*(5+3*tan2phi+eta2-9*tan2phi*eta2)
    IX = tanphi/(720*rho*nu5)*(61+90*tan2phi+45*tan4phi)
    X = secphi/nu
    XI = secphi/(6*nu3)*(nu/rho+2*tan2phi)
    XII = secphi/(120*nu5)*(5+28*tan2phi+24*tan4phi)
    XIIA = secphi/(5040*nu7)*(61+662*tan2phi+1320*tan4phi+720*tan6phi)

    dE = (E-E0)
    dE2 = dE*dE
    dE3 = dE2*dE
    dE4 = dE2*dE2
    dE5 = dE3*dE2
    dE6 = dE4*dE2
    dE7 = dE5*dE2
    phi = phi - VII*dE2 + VIII*dE4 - IX*dE6
    lambdaa = lambdaa0 + X*dE - XI*dE3 + XII*dE5 - XIIA*dE7

    phi = ToDegrees(phi)
    lambdaa = ToDegrees(lambdaa)

    print(phi,lambdaa)
    print(convert_lonlat(phi,lambdaa))
