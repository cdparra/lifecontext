class MediaController < ApplicationController
  
  def index
    list
    render('list')
  end
  
  def list
    @media = Media.all
  end

  def new
    @medium = Media.new
  end
  
  def create
    @medium = Media.new
    @medium.build_location
    @medium.build_fuzzyDate
    populateOrUpdateMedia(@medium, params[:media])
    
    if @medium.save
      flash[:notice]="Medium created!"
      redirect_to(media_index_path)
    else
      render('new')
    end 
  end

  def edit
    @medium = Media.find(params[:id], :include => [:location, :fuzzyDate])
  end

  def update
    @medium = Media.find(params[:id], :include => [:location, :fuzzyDate])
    
    populateOrUpdateMedia(@medium,params[:media])
    
    if @medium.save
      flash[:notice]="Medium updated !"
      redirect_to media_index_path
    else
      render('edit')
    end 
  end 
  
  def populateOrUpdateMedia(medium,data)
    medium.caption = data[:caption]
    medium.text = data[:text]
    medium.media_type = data[:media_type]
    medium.media_url = data[:media_url]
    medium.source = data[:source]
    medium.source_url = data[:source_url]
    medium.is_public = data[:is_public]
    populateOrUpdateLocation(medium.location,data[:location])
    populateOrUpdateFuzzyDate(medium.fuzzyDate,data[:fuzzy_date])
  end
  
  def populateOrUpdateLocation(location,data)
    location.location_textual = data[:location_textual]
    location.accuracy = data[:accuracy]
    location.name = data[:name]
    location.description = data[:description]
    location.country = data[:country]
    location.region = data[:region]
    location.city = data[:city]
    location.coordinates_trust = data[:coordinates_trust]
    location.lat = data[:lat]
    location.lon = data[:lon]
    location.locale = data[:locale]
    location.radius = data[:radius]
  end
  
  def populateOrUpdateFuzzyDate(date,data)
    date.textual_date = data[:textual_date]
    date.exact_date = data[:exact_date]
    date.decade = data[:decade]
    date.year = data[:year]
    date.season = data[:season]
    date.month = data[:month]
    date.day = data[:day]
    date.day_name = data[:day_name]
    date.day_part = data[:day_part]
    date.hour = data[:hour]
    date.minute = data[:minute]
    date.second = data[:second]
    date.accuracy = data[:accuracy]
    date.locale = data[:locale]
  end

end

