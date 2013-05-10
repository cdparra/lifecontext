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

ActiveRecord::Base.logger = Logger.new(STDOUT)

Rabl.register!

#Geocoder.configure(:timeout => 10000000) # da cambiare!!! (la vm fa schifo come velocita')

def getGoogleCoordinates(loc)
  @place=City.new
  s = Geocoder.search(:loc)
  @place.lat=s[0].latitude
  @place.lon=s[0].longitude
  return @place
end

get '/' do
    res = { :success => false,
                :info => "Wrong request: you need to specify a method",
              }.to_json  
  halt 500, {'Content-Type' => 'application/json'}, res
end

get '/generalBooklet/media' do
  
  if params[:decade].length == params[:place].length
    
    
  else
    res = { :success => false,
                :info => "Wrong number of arguments: decade list and place list must have the same length!",
              }.to_json  
    halt 500, {'Content-Type' => 'application/json'}, res
  end
end

after do
  ActiveRecord::Base.connection.close
end
