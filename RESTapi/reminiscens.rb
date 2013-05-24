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

class ReminiscensAPI < Sinatra::Application

  ActiveRecord::Base.logger = Logger.new(STDOUT)

  Rabl.register!

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
    halt 400, {'Content-Type' => 'application/json'}, res
  end
end

require_relative 'v1'
require_relative 'v2'
require_relative 'simpleCalls'