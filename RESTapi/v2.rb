require './method_utils'
require './mec'

class ReminiscensAPI < Sinatra::Application

  #v2 api

  def isInRadius(lats,lons)
  
    l = lats.length
    points = Array.new(l)
    i = 0
  
    lats.zip(lons).each do |lat,lon|
      p = toUTM(lat.to_f, lon.to_f)
      points[i] = p
      i += 1
    end
    
    circle = pointArrayDistance(points)
  
    return circle.radius <= 50000
  end

  get '/v2/generalBooklet/media' do
  
    lats = params[:lat]
    lons = params[:lon]
    decades = params[:decade]
  
    if lats.length == lons.length && lons.length == decades.length && decades.length > 0
      
      l = decades.length
    
      if isInRadius(lats,lons)
      
        if decades[0] == decades[l/2] && decades[l-1] == decades[l/2]
          orderString = "abs(decade - #{decades[l/2]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"       
          @media = Media.joins(:contextIndex => :city).where("Context_Index.media_id IS NOT NULL").order(orderString).limit(5)
        elsif l <= 3
          size = 5
          @media = Array.new
          decades.each_with_index do |dec,k|
            i = (size/(l-k)).floor
            orderString = "abs(decade - #{(dec)}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"       
            @media += Media.joins(:contextIndex => :city).where("Context_Index.media_id IS NOT NULL").order(orderString).limit(i)
            size = size - i
          end
        else
          orderString_1 = "abs(decade - #{decades[0]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"
          orderString_2 = "abs(decade - #{decades[l/2]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"       
          orderString_3 = "abs(decade - #{decades[l-1]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"             
          @media = Media.joins(:contextIndex => :city).where("Context_Index.media_id IS NOT NULL").order(orderString_1).limit(2)
                  + Media.joins(:contextIndex => :city).where("Context_Index.media_id IS NOT NULL").order(orderString_1).limit(1)
                  + Media.joins(:contextIndex => :city).where("Context_Index.media_id IS NOT NULL").order(orderString_1).limit(2)
        end
      else
        if l <= 3
          size = 5
          @media = Array.new
          decades.each_with_index do |dec,k|
            i = (size/(l-k)).floor
            orderString = "abs(decade - #{(dec)}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[k]})),2) + POW((0.0174 * (lon - #{lons[k]}) * COS(#{lats[k]})),2))), distance, coordinates_trust DESC"       
            @media += Media.joins(:contextIndex => :city).where("Context_Index.media_id IS NOT NULL").order(orderString).limit(i)
            size = size - i
          end
        else
          orderString_1 = "abs(decade - #{decades[0]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[0]})),2) + POW((0.0174 * (lon - #{lons[0]}) * COS(#{lats[0]})),2))), distance, coordinates_trust DESC"
          orderString_2 = "abs(decade - #{decades[l/2]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"       
          orderString_3 = "abs(decade - #{decades[l-1]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l-1]})),2) + POW((0.0174 * (lon - #{lons[l-1]}) * COS(#{lats[l-1]})),2))), distance, coordinates_trust DESC"             
          @media = Media.joins(:contextIndex => :city).where("Context_Index.media_id IS NOT NULL").order(orderString_1).limit(2)
                  + Media.joins(:contextIndex => :city).where("Context_Index.media_id IS NOT NULL").order(orderString_1).limit(1)
                  + Media.joins(:contextIndex => :city).where("Context_Index.media_id IS NOT NULL").order(orderString_1).limit(2)
        end
      end
      render :rabl, :context_media, :format => "json"
    else
      res = { :success => false,
                :info => "decade, lat and lon lists must have the same length, that is more than 0",
              }.to_json  
      halt 400, {'Content-Type' => 'application/json'}, res
    end
  end

  get '/v2/generalBooklet/events' do
  
    lats = params[:lat]
    lons = params[:lon]
    decades = params[:decade]
  
    if lats.length == lons.length && lons.length == decades.length && decades.length > 0
      
      l = decades.length
    
      if isInRadius(lats,lons)
      
        if decades[0] == decades[l/2] && decades[l-1] == decades[l/2]
          orderString = "abs(decade - #{decades[l/2]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"       
          @events = Event.joins(:contextIndex => :city).where("Context_Index.event_id IS NOT NULL").order(orderString).limit(5)
        elsif l <= 3
          size = 5
          @events = Array.new
          decades.each_with_index do |dec,k|
            i = (size/(l-k)).floor
            orderString = "abs(decade - #{(dec)}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"       
            @events += Event.joins(:contextIndex => :city).where("Context_Index.event_id IS NOT NULL").order(orderString).limit(i)
            size = size -i
          end
        else
          orderString_1 = "abs(decade - #{decades[0]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"
          orderString_2 = "abs(decade - #{decades[l/2]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"       
          orderString_3 = "abs(decade - #{decades[l-1]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"             
          @events = Event.joins(:contextIndex => :city).where("Context_Index.event_id IS NOT NULL").order(orderString_1).limit(2)
                  + Event.joins(:contextIndex => :city).where("Context_Index.event_id IS NOT NULL").order(orderString_1).limit(1)
                  + Event.joins(:contextIndex => :city).where("Context_Index.event_id IS NOT NULL").order(orderString_1).limit(2)
        end
      else
        if l <= 3
          size = 5
          @events = Array.new
          decades.each_with_index do |dec,k|
            i = (size/(l-k)).floor
            orderString = "abs(decade - #{(dec)}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[k]})),2) + POW((0.0174 * (lon - #{lons[k]}) * COS(#{lats[k]})),2))), distance, coordinates_trust DESC"       
            @events += Event.joins(:contextIndex => :city).where("Context_Index.event_id IS NOT NULL").order(orderString).limit(i)
            size = size - i
          end
        else
          orderString_1 = "abs(decade - #{decades[0]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[0]})),2) + POW((0.0174 * (lon - #{lons[0]}) * COS(#{lats[0]})),2))), distance, coordinates_trust DESC"
          orderString_2 = "abs(decade - #{decades[l/2]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l/2]})),2) + POW((0.0174 * (lon - #{lons[l/2]}) * COS(#{lats[l/2]})),2))), distance, coordinates_trust DESC"       
          orderString_3 = "abs(decade - #{decades[l-1]}), (6378.7*sqrt(POW((0.0174 * (lat - #{lats[l-1]})),2) + POW((0.0174 * (lon - #{lons[l-1]}) * COS(#{lats[l-1]})),2))), distance, coordinates_trust DESC"             
          @events = Event.joins(:contextIndex => :city).where("Context_Index.event_id IS NOT NULL").order(orderString_1).limit(2)
                  + Event.joins(:contextIndex => :city).where("Context_Index.event_id IS NOT NULL").order(orderString_1).limit(1)
                  + Event.joins(:contextIndex => :city).where("Context_Index.event_id IS NOT NULL").order(orderString_1).limit(2)
        end
      end
      render :rabl, :context_events, :format => "json"
    else
      res = { :success => false,
                :info => "decade, lat and lon lists must have the same length, that is more than 0",
              }.to_json  
      halt 400, {'Content-Type' => 'application/json'}, res
    end
  end

  get '/v2/generalBooklet/works' do
  
    decades = params[:decade]
    l = decades.length

      if l != 0
        if l <= 3
          size = 5
          @mediaMDs = Array.new
          decades.each_with_index do |dec,k|
            i = (size/(l-k)).floor
            orderString = "abs(decade - #{(dec)})"
            @mediaMDs += Work.joins(:contextIndex).where("Context_Index.work_id IS NOT NULL").order(orderString).limit(i)
            size = size - i
          end
        else
          orderString_1 = "abs(decade - #{decades[0]})"
          orderString_2 = "abs(decade - #{decades[l/2]})"
          orderString_3 = "abs(decade - #{decades[l-1]})"
          @mediaMDs = Work.joins(:contextIndex).where("Context_Index.work_id IS NOT NULL").order(orderString_1).limit(2) + Work.joins(:contextIndex).where("Context_Index.work_id IS NOT NULL").order(orderString_1).limit(1) + Work.joins(:contextIndex).where("Context_Index.work_id IS NOT NULL").order(orderString_1).limit(2)
        end
        render :rabl, :context_works, :format => "json"
      else
        res = { :success => false,
                  :info => "decade list must be longer than 0",
                }.to_json  
        halt 400, {'Content-Type' => 'application/json'}, res
      end
  end
  
get '/v2/generalBooklet/people' do

  decades = params[:decade]
  l = decades.length

    if l != 0
      if l <= 3
        size = 5
        @mediaMDs = Array.new
        decades.each_with_index do |dec,k|
          i = (size/(l-k)).floor
          orderString = "abs(decade - #{(dec)})"
          @mediaMDs += FamousPeople.joins(:contextIndex).where("Context_Index.famous_id IS NOT NULL").order(orderString).limit(i)
          size = size - i
        end
      else
        orderString_1 = "abs(decade - #{decades[0]})"
        orderString_2 = "abs(decade - #{decades[l/2]})"
        orderString_3 = "abs(decade - #{decades[l-1]})"
        @mediaMDs = FamousPeople.joins(:contextIndex).where("Context_Index.famous_id IS NOT NULL").order(orderString_1).limit(2)
                + FamousPeople.joins(:contextIndex).where("Context_Index.famous_id IS NOT NULL").order(orderString_1).limit(1)
                + FamousPeople.joins(:contextIndex).where("Context_Index.famous_id IS NOT NULL").order(orderString_1).limit(2)
      end
      render :rabl, :context_works, :format => "json"
    else
      res = { :success => false,
                :info => "decade list must be longer than 0",
              }.to_json  
      halt 400, {'Content-Type' => 'application/json'}, res
    end
end
  

  after do
    ActiveRecord::Base.connection.close
  end
end
