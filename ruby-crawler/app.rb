require 'flickraw'
require 'active_record'
require 'active_support/core_ext'
require 'geocoder'
load 'db/models/media.rb'
load 'db/models/location.rb'
load 'db/models/fuzzy_date.rb'
load 'config/settings.rb'

flickrTag = "Trento"
min_date = "1900-01-01"
max_date = "2010-12-12"

DEBUG = false

def getGoogleGeoData(location,locString)
  s = Geocoder.search("Trento")
  if DEBUG
    puts s
  end
  begin
    location.lat = s[0].latitude
  	location.lon = s[0].longitude
  rescue Exception => e
  end
  begin
    location.name = locString
  rescue Exception => e
  end
  begin
    location.city = s[0].city
  rescue Exception => e
  end
  begin
    location.region = s[0].state
  rescue Exception => e
  end
  begin
    location.country = s[0].country
  rescue Exception => e
  end
  
  if location.lat == "0" && location.lon == "0"
  	location.coordinates_trust = 0
  end
  
end

def getFlickrGeoData(location,id)
  data = flickr.photos.geo.getLocation :photo_id => id
  begin
    location.name = data.location.locality
  rescue Exception => e
  end
  begin
    location.city = data.location.county
  rescue Exception => e
  end
  begin
    location.region = data.location.region
  rescue Exception => e
  end
  begin
    location.country = data.location.country
  rescue Exception => e
  end

end

def splitDate(fDate,dateString)

  begin
    convDate = Time.new(dateString)
    fDate.locale = 'it'
    fDate.exact_date = convDate.strftime("%F").to_s
    fDate.year = convDate.year
    fDate.decade = (convDate.year.slice(0,3)) + "0"
    fDate.accuracy = 4
    fDate.month = convDate.month
    fDate.day = convDate.day
    fDate.day_name = convDate.strftime("%A").to_s
    fDate.accuracy = 9
    fDate.hour = convDate.hour
    fDate.minute = convDate.min
    fDate.second = convDate.sec

    intHour = fDate.hour.to_i;
    if intHour >= 05 && intHour < 12
      fDate.day_part = "mattino";
    elsif intHour >= 12 && intHour < 19
      fDate.day_part = "pomeriggio";
    elsif intHour >= 19 && intHour < 24
      fDate.day_part = "sera";
    elsif intHour >= 24 && intHour < 05
      fDate.day_part = "notte";
    end
    fDate.accuracy = 11
  rescue Exception => e
  fDate.accuracy = 1
  fDate.textual_date = dateString
  end

end

def createPicture(data)
  info = flickr.photos.getInfo :photo_id => data.id

  photo = Media.create do |p|
    p.caption = info.title.gsub("'","\\\\'")
    p.media_url = "http://farm#{data.farm}.static.flickr.com/#{data.server}/#{data.id}_#{data.secret}_z.jpg"
    p.media_type = "photo"
    p.source = "flickr"
    p.source_url = "http://www.flickr.com/photos/#{data.owner}/#{data.id}"
    p.is_public = 1
    p.locale = "ita"
    p.last_update = Time.now
    p.build_fuzzyDate()
    splitDate(p.fuzzyDate,info.dates.taken)
    if data.accuracy!=0
      p.build_location(:location_textual => :flickrTag, :lat => data.latitude, :lon => data.longitude, :accuracy => data.accuracy)
      #chiamo api flickr per beccarmi i dati specifici
      if DEBUG
        puts "FLICKR GEO per foto con id '#{data.id}'"
      end
      getFlickrGeoData(p.location,data.id)
    else
      p.build_location(:location_textual => :flickrTag, :accuracy => 2, :radius => 0)
      #chiamo google maps per sapere qualcosa dal tag
      if DEBUG
        puts "GOOGLE MAPS per foto con id '#{data.id}'"
      end
      getGoogleGeoData(p.location,:flickrTag)
      p.location.save
      puts p.location.lat
    end
  end
  return photo
end

FlickRaw.api_key="b70a9e175b81d1e4cd19fd652f0af12a"
FlickRaw.shared_secret="77b1f598e7f67191"

list   = flickr.photos.search :tags => flickrTag, :license => "1%2C2%2C4%2C5%2C7", :min_taken_date => min_date,
:max_taken_date => max_date, :extras => "geo"

count=1
if DEBUG
  puts "siamo in #{list.length}"
end

list.each do |i|

  photo = createPicture(i)
  tmpPhoto = Media.find_by_media_url(photo.media_url)
  if tmpPhoto.nil?
    if DEBUG
  	  puts "photo id #{i.id} saved!"
    end
  	photo.save
  else
    if DEBUG
      puts "photo id #{i.id} updated!"
    end
    photo.media_id = tmpPhoto.media_id
	tmpPhoto.update_attributes(photo.attributes)
  
  if DEBUG
	  puts photo.location.location_id
  end
  end
  
  if DEBUG	
    puts count
  end
  count=count+1
end
