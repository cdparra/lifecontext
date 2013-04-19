class MediaApiController < ApplicationController
  
  respond_to :json
  
  def index
    @media = Media.all
  end
  
  def create
    data = JSON.parse(request.body.read)
    @medium = Media.new
    @medium.build_location
    @medium.build_fuzzyDate
    populateOrUpdateMedia(@medium, data)
    
    if @medium.save
      @response = OpenStruct.new({ :success => true,
                    :info => "Media created",
                    })
      render "media_api/response"
    else
      @response = OpenStruct.new({ :success => false,
                    :info => "Media NOT created, retry",
                    })    
      render "media_api/response"
    end 
  end

  def update
    data = JSON.parse(request.body.read)
    @medium = Media.find(params[:id], :include => [:location, :fuzzyDate])
    
    populateOrUpdateMedia(@medium,data)
    
    if @medium.save
      @response = OpenStruct.new({ :success => true,
                    :info => "Media updated",
                    })
      render "media_api/response"

    else
      @response = OpenStruct.new({ :success => false,
                    :info => "Media NOT updated",
                    })
      render "media_api/response"
    end 
  end 
  
  def populateOrUpdateMedia(medium,data)
    medium.caption = data["caption"]
    medium.text = data["text"]
    medium.media_type = data["media_type"]
    medium.media_url = data["media_url"]
    medium.source = data["source"]
    medium.source_url = data["source_url"]
    medium.is_public = data["is_public"]
    puts data["location"]
    if data["location"] != nil
      populateOrUpdateLocation(medium.location,data["location"])
    end
    if data["fuzzy_date"] != nil
      populateOrUpdateFuzzyDate(medium.fuzzyDate,data["fuzzy_date"])
    end
  end
  
  def populateOrUpdateLocation(location,data)
    location.location_textual = data["location_textual"]
    location.accuracy = data["accuracy"]
    location.name = data["name"]
    location.description = data["description"]
    location.country = data["country"]
    location.region = data["region"]
    location.city = data["city"]
    location.coordinates_trust = data["coordinates_trust"]
    location.lat = data["lat"]
    location.lon = data["lon"]
    location.locale = data["locale"]
    location.radius = data["radius"]
  end
  
  def populateOrUpdateFuzzyDate(date,data)
    date.textual_date = data["textual_date"]
    date.exact_date = data["excat_date"]
    date.decade = data["decade"]
    date.year = data["year"]
    date.season = data["season"]
    date.month = data["month"]
    date.day = data["day"]
    date.day_name = data["day_name"]
    date.day_part = data["day_part"]
    date.hour = data["hour"]
    date.minute = data["minute"]
    date.second = data["second"]
    date.accuracy = data["accuracy"]
    date.locale = data["locale"]
  end

end

