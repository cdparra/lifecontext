$pi = 3.14159265358979

$sm_a = 6378137.0

$sm_b = 6356752.314

$UTMScaleFactor = 0.9996

def DegToRad(deg)
    return (deg / 180.0 * $pi)
end

def ArcLengthOfMeridian(phi)

    n = ($sm_a - $sm_b) / ($sm_a + $sm_b)

    alpha = (($sm_a + $sm_b) / 2.0) * (1.0 + ((n**2.0) / 4.0) + ((n**4.0) / 64.0))

    beta = (-3.0 * n / 2.0) + (9.0 * (n**3.0) / 16.0) + (-3.0 * (n**5.0) / 32.0)

    gamma = (15.0 * (n**2.0) / 16.0) + (-15.0 * (n**4.0) / 32.0)

    delta = (-35.0 * (n**3.0) / 48.0) + (105.0 * (n**5.0) / 256.0)

    epsilon = (315.0 * (n**4.0) / 512.0)

result = alpha * (phi + (beta * Math.sin(2.0 * phi)) + (gamma * Math.sin(4.0 * phi)) + (delta * Math.sin(6.0 * phi)) + (epsilon * Math.sin(8.0 * phi)))

return result
end

def UTMCentralMeridian(zone)

    cmeridian = DegToRad(-183.0 + (zone * 6.0))

    return cmeridian
end

def MapLatLonToXY(phi, lambda, lambda0, xy)

    ep2 = (($sm_a**2.0) - ($sm_b**2.0)) / ($sm_b**2.0)

    nu2 = ep2 * (Math.cos(phi)**2.0)

    n1 = ($sm_a**2.0) / ($sm_b * Math.sqrt(1 + nu2))

    t = Math.tan(phi)

    t2 = t * t

    tmp = (t2 * t2 * t2) - (t**6.0)

    l = lambda - lambda0

    l3coef = 1.0 - t2 + nu2

    l4coef = 5.0 - t2 + 9 * nu2 + 4.0 * (nu2 * nu2)

    l5coef = 5.0 - 18.0 * t2 + (t2 * t2) + 14.0 * nu2 - 58.0 * t2 * nu2

    l6coef = 61.0 - 58.0 * t2 + (t2 * t2) + 270.0 * nu2 - 330.0 * t2 * nu2

    l7coef = 61.0 - 479.0 * t2 + 179.0 * (t2 * t2) - (t2 * t2 * t2)

    l8coef = 1385.0 - 3111.0 * t2 + 543.0 * (t2 * t2) - (t2 * t2 * t2)

    xy[0] = n1 * Math.cos(phi) * l + (n1 / 6.0 * (Math.cos(phi)**3.0) * l3coef * (l**3.0)) + (n1 / 120.0 * (Math.cos(phi)**5.0) * l5coef * (l**5.0)) + (n1 / 5040.0 * (Math.cos(phi)**7.0) * l7coef * (l**7.0))

    xy[1] = ArcLengthOfMeridian(phi) + (t / 2.0 * n1 * (Math.cos(phi)**2.0) * (l**2.0)) + (t / 24.0 * n1 * (Math.cos(phi)**4.0) * l4coef * (l**4.0)) + (t / 720.0 * n1 * (Math.cos(phi)**6.0) * l6coef * (l**6.0)) + (t / 40320.0 * n1 * (Math.cos(phi)**8.0) * l8coef * (l**8.0))
end

def LatLonToUTMXY(lat, lon, zone, xy)

    MapLatLonToXY(lat, lon, UTMCentralMeridian(zone), xy)

    xy[0] = xy[0] * $UTMScaleFactor + 500000.0

    xy[1] = xy[1] * $UTMScaleFactor

    if xy[1] < 0.0
        xy[1] = xy[1] + 10000000.0
    end

    return zone
end


def toUTM(lat,lon)
  
    xy = Array.new(2)

    zone = ((lon + 180.0) / 6).floor + 1
    zone = LatLonToUTMXY(DegToRad(lat), DegToRad(lon), zone, xy)

    return Point.new(xy[0],xy[1])
end