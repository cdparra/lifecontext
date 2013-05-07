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

load 'config/settings.rb'
load 'db/models/models.rb'

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
  @response = "Wrong request: you need to specify a method"
  content_type :json
  @response.to_json
end

#Media

  ##~ sapi = source2swagger.namespace("lifecontext")
  ##~ sapi.basePath = "http://test.reminiscens.me/lifecontext/api"
  ##~ sapi.swaggerVersion = "0.1a"
  ##~ sapi.apiVersion = "1.0"

  ##~ sapi = source2swagger.namespace("lifecontext")
  ##~ a = sapi.apis.add
  ## 
  ##~ a.set :path => "/media", :format => "json"
  ##~ a.description = "Media in our knowledge base"
  ##
  ##  declaring errors
  ##
  ##~ err = a.errorResponses.add 
  ##~ err.set :reason => "failure to sanitize: \"input\"", :code => 422
  ##~ a.errorResponses.add :reason => "failure to sanitize: \"input\", returns empty set", :code => 422
  ##~ a.errorResponses.add :reason => "access denied, either your access credentials are incorrect or you are about the limits of your quota", :code => 403
  ##~ a.errorResponses.add :reason => "API down", :code => 500
  ##~ 
  ##~ op = a.operations.add   
  ##~ op.set :httpMethod => "GET", :tags => ["production"], :nickname => "get_media", :deprecated => false
  ##~ op.summary = "Returns the sentiment values of a given word"  
  ##~ op.parameters.add :name => "decade", :description => "Media was taken in the decade", :dataType => "string", :allowMultiple => false, :required => true, :paramType => "query"
  ##~ op.parameters.add :name => "place", :description => "Media was taken nearby the place", :dataType => "string", :allowMultiple => false, :required => true, :paramType => "query"
  ##~ op.parameters.add :name => "radius", :description => "Media was taken in the radius of the place", :dataType => "string", :allowMultiple => false, :required => false, :paramType => "query"
  ## 
get '/media' do

  if params[:decade]!=nil && params[:decade].match(/\A(18|19|20)\d0\z/)

    if params[:lat]!=nil && params[:lon]!=nil

      if params[:radius]!=nil
        @radius=params[:radius]
      else
        @radius=0
      end

      @media=Media.joins(:fuzzyDate, :location).where("is_public = 1 AND decade = ?
        AND (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
        POW((0.0174 * (lon - ?) * COS(?)),2))) <= ?",
        params[:decade], params[:lat], params[:lon], params[:lat], @radius)

    elsif params[:place]!=nil

      if params[:radius]!=nil
        @radius=params[:radius]
      else
        @radius=0
      end

      @place=City.find(:first, :conditions => ["city_name = ? ", params[:place]])

      if @place==nil
        @place=getGoogleCoordinates(params[:place])
      end

      @media=Media.joins(:fuzzyDate, :location).where("is_public = 1 AND decade = ? 
        AND (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
        POW((0.0174 * (lon - ?) * COS(?)),2))) <= ?",
        params[:decade], @place.lat, @place.lon, @place.lat, @radius)

    else

      @media=Media.joins(:fuzzyDate).where("is_public = 1 AND decade = ? ", params[:decade])

    end

  elsif params[:lat]!=nil && params[:lon]!=nil

    if params[:radius]!=nil
      @radius=params[:radius]
    else
      @radius=0
    end

    @media=Media.joins(:location).where("is_public = 1 AND (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
      POW((0.0174 * (lon - ?) * COS(?)),2))) <= ?",
      params[:lat], params[:lon], params[:lat], @radius)

  elsif params[:place]!=nil

    if params[:radius]!=nil
      @radius=params[:radius]
    else
      @radius=0
    end

    @place=City.find(:first, :conditions => ["city_name = ? ", params[:place]])

    if @place==nil
      @place=getGoogleCoordinates(params[:place])
    end

    @media=Media.joins(:location).where("is_public = 1 AND (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
      POW((0.0174 * (lon - ?) * COS(?)),2))) <= ?",
      @place.lat, @place.lon, @place.lat, @radius)
  end

  if defined?(@media)
    if params[:dev]!=nil
      Rabl.render(@media, 'media.rabl', view_paths => 'views/dev/', :format => :json, :scope => self)
    else
      render :rabl, :media, :format => "json"
    end
  else
    render :rabl, :param_errors, :format => "json"
  end

end

#Events

get '/events' do

  if params[:decade]!=nil && params[:decade].match(/\A(18|19|20)\d0\z/)

    if params[:lat]!=nil && params[:lon]!=nil

      if params[:radius]!=nil
        @radius=params[:radius]
      else
        @radius=0
      end

      @events=Event.joins(:fuzzyDate, :location).where("AND decade = ?
        AND (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
        POW((0.0174 * (lon - ?) * COS(?)),2))) <= ?",
        params[:decade], params[:lat], params[:lon], params[:lat], @radius)

    elsif params[:place]!=nil

      if params[:radius]!=nil
        @radius=params[:radius]
      else
        @radius=0
      end

      @place=City.find(:first, :conditions => ["city_name = ? ", params[:place]])

      if @place==nil
        @place=getGoogleCoordinates(params[:place])
      end

      @events=Event.joins(:fuzzyDate, :location).where("decade = ?
        AND (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
        POW((0.0174 * (lon - ?) * COS(?)),2))) <= ?",
        params[:deacde], @place.lat, @place.lon, @place.lat, @radius)

    else

      @events=Event.joins(:fuzzyDate).where("decade = ? ", params[:decade])

    end

  elsif params[:lat]!=nil && params[:lon]!=nil

    if params[:radius]!=nil
      @radius=params[:radius]
    else
      @radius=0
    end

    @events=Event.joins(:location).where("(6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
      POW((0.0174 * (lon - ?) * COS(?)),2))) <= ?",
      params[:lat], params[:lon], params[:lat], @radius)

  elsif params[:place]!=nil

    if params[:radius]!=nil
      @radius=params[:radius]
    else
      @radius=0
    end

    @place=City.find(:first, :conditions => ["city_name = ? ", params[:place]])

    if @place==nil
      @place=getGoogleCoordinates(params[:place])
    end

    @events=Event.joins(:location).where("(6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
      POW((0.0174 * (lon - ?) * COS(?)),2))) <= ?",
      @place.lat, @place.lon, @place.lat, @radius)
  end

  if defined?(@events)
    render :rabl, :events, :format => "json"
  else
    render :rabl, :param_errors, :format => "json"
  end

end

#Works
get '/works' do

  if params[:decade]!=nil && params[:decade].match(/\A(18|19|20)\d0\z/)

    @mediaMDs=MediaMetadata.joins(:fuzzyDate).where("decade = ? ", params[:decade])
  end

  if defined?(@mediaMDs)
    render :rabl, :works, :format => "json"
  else
    render :rabl, :param_errors, :format => "json"
  end


end

#People
get "/people" do
  if params[:type]!=nil
      if params[:decade]!=nil && params[:decade].match(/\A(18|19|20)\d0\z/)

      if params[:lat]!=nil && params[:lon]!=nil

        if params[:radius]!=nil
          @radius=params[:radius]
        else
          @radius=0
        end

        @part=Participant.find_by_sql([
        "SELECT pe.*
        FROM Participant pa, Person pe
        WHERE pa.person_id=pe.person_id AND
        pe.famous = 1 AND pa.life_event_id IN (SELECT life_event_id FROM Life_Event l,
        Location lo, Fuzzy_Date f WHERE
        l.location_id=lo.location_id AND
        l.fuzzy_startdate=f.fuzzy_date_id AND
        decade = ? AND
        (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
        POW((0.0174 * (lon - ?) * COS(?)),2))) <= ? AND
        type = ? )",
        params[:decade], params[:lat], params[:lon], params[:lat], @radius, params[:type]
        ])

      elsif params[:place]!=nil

        if params[:radius]!=nil
          @radius=params[:radius]
        else
          @radius=0
        end

        @place=City.find(:first, :conditions => ["city_name = ? ", params[:place]])

        if @place==nil
          @place=getGoogleCoordinates(params[:place])
        end

        @part=Partcipant.find_by_sql([
        "SELECT pe.*
        FROM Participant pa, Person pe
        WHERE pa.person_id=pe.person_id AND
        pe.famous = 1 AND pa.life_event_id IN (SELECT life_event_id FROM Life_Event l,
        Location lo, Fuzzy_Date f WHERE
        l.location_id=lo.location_id AND
        l.fuzzy_startdate=f.fuzzy_date_id AND
        decade = ? AND
        (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
        POW((0.0174 * (lon - ?) * COS(?)),2))) <= ? AND
        type = ? )",
        params[:decade], @place.lat, @place.lon, @place.lat, @radius, params[:type]
        ])

      else

        @part=Participant.find_by_sql([
          "SELECT pe.*
          FROM Participant pa, Person pe
          WHERE pa.person_id=pe.person_id AND
          pe.famous = 1 AND pa.life_event_id IN
          (SELECT life_event_id FROM Life_Event l,
          Fuzzy_Date f WHERE l.fuzzy_startdate=f.fuzzy_date_id AND
          type = ? AND decade = ? )", params[:type], params[:decade]
        ])

      end

    elsif params[:lat]!=nil && params[:lon]!=nil

      if params[:radius]!=nil
        @radius=params[:radius]
      else
        @radius=0
      end

      @part=Participant.find_by_sql([
        "SELECT pe.*
        FROM Participant pa, Person pe
        WHERE pa.person_id=pe.person_id AND
        pe.famous = 1 AND pa.life_event_id IN (SELECT life_event_id FROM Life_Event l,
        Location lo WHERE
        l.location_id=lo.location_id AND
        (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
        POW((0.0174 * (lon - ?) * COS(?)),2))) <= ? AND
        type = ? )",
        params[:lat], params[:lon], params[:lat], @radius, params[:type]
        ])

    elsif params[:place]!=nil

      if params[:radius]!=nil
        @radius=params[:radius]
      else
        @radius=0
      end

      @place=City.find(:first, :conditions => ["city_name = ? ", params[:place]])

      if @place==nil
        @place=getGoogleCoordinates(params[:place])
      end

      @part=Participant.find_by_sql([
        "SELECT pe.*
        FROM Participant pa, Person pe
        WHERE pa.person_id=pe.person_id AND
        pe.famous = 1 AND pa.life_event_id IN (SELECT life_event_id FROM Life_Event l,
        Location lo WHERE
        l.location_id=lo.location_id AND
        (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
        POW((0.0174 * (lon - ?) * COS(?)),2))) <= ? AND
        type = ? )",
        @place.lat, @place.lon, @place.lat, @radius, params[:type]
        ])

    end
    
    if defined?(@part)
      render :rabl, :people, :format => "json"
    else
      render :rabl, :param_errors, :format => "json"
    end
    
  else
      render :rabl, :missing_type_error, :format => "json"
  end

end
