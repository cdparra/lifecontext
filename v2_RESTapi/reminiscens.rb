require 'rubygems'
require 'sinatra'
require 'sass'
require 'mysql2'
require 'rabl'
require 'oj'
require 'active_record'
require 'active_support/core_ext'
require 'active_support/inflector'
require 'builder'
require 'geocoder'

require './config/settings'
require './models/models'
require './method_utils'
require './mec'

ActiveRecord::Base.logger = Logger.new(STDOUT)

Rabl.register!

def getGoogleCoordinates(loc)
  @place=City.new
  s = Geocoder.search(:loc)
  @place.lat=s[0].latitude
  @place.lon=s[0].longitude
  return @place
end

def areInRadius(lats,lons)
  
  l = lats.length
  points = Array.new(l)
  i = 0
  
  lats.zip(lons).each do |lat,lon|
    p = toUTM(lat.to_f, lon.to_f)
    points[i] = p
    i += 1
  end
    
  radius = pointArrayDistance(points)
  
  return radius < 50000
end

get '/' do
    res = { :success => false,
                :info => "Wrong request: you need to specify a method",
              }.to_json  
  halt 500, {'Content-Type' => 'application/json'}, res
end

get '/generalBooklet/media' do
  
  lats = params[:lat]
  lons = params[:lon]
  
  if lats.length == lons.length
      
    if areInRadius(lats,lons)
    
    else
    
    end
  else
    res = { :success => false, :info => "lat and lon lists must have the same length" }.to_json
  end
end

after do
  ActiveRecord::Base.connection.close
end
