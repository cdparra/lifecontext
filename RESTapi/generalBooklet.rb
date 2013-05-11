class ReminiscensAPI < Sinatra::Application
  
  #bookletResources

  get '/generalBooklet/media' do

    decade = params[:decade]
    lat = params[:lat]
    lon = params[:lon]
  
    if decade != nil && decade.match(/\A(18|19|20)\d0\z/) && decade.to_i < 2050 && decade.to_i >= 1900
    
      if lat != nil && lon != nil && lat.match(/^(\-?\d+(\.\d+)?),*(\-?\d+(\.\d+)?)$/) && lon.match(/^(\-?\d+(\.\d+)?),*(\-?\d+(\.\d+)?)$/)
  
        orderString = "abs(decade - #{decade}), (6378.7*sqrt(POW((0.0174 * (lat - #{lat})),2) + POW((0.0174 * (lon - #{lon}) * COS(#{lat})),2))), distance, coordinates_trust DESC"
        
        @media = Media.joins(:contextIndex => :city).where("Context_Index.media_id IS NOT NULL").order(orderString).limit(5)
    
      else
        @media = Media.joins(:contextIndex).where("Context_Index.media_id IS NOT NULL").order("abs(decade - #{decade})").limit(5)
      end

      render :rabl, :context_media, :format => "json"

    else
      res = { :success => false,
                :info => "Wrong request: you need to specify a decade",
              }.to_json  
      halt 500, {'Content-Type' => 'application/json'}, res
    end
  end

  get '/generalBooklet/works' do

    decade = params[:decade]
  
    if decade != nil && decade.match(/\A(18|19|20)\d0\z/) && decade.to_i < 2050 && decade.to_i >= 1900
  
      orderString = "abs(decade - #{decade})"
  
      @mediaMDs = MediaMetadata.joins(:contextIndex).where("Context_Index.media_metadata_id IS NOT NULL AND author IS NOT NULL AND resource_url IS NOT NULL").order(orderString).limit(5)

      render :rabl, :context_works, :format => "json"
  
    else
    
      res = { :success => false,
                  :info => "Wrong request: you need to specify a decade",
                }.to_json  
      halt 500, {'Content-Type' => 'application/json'}, res
    
    end

  end


  get '/generalBooklet/events' do

    decade = params[:decade]
    lat = params[:lat]
    lon = params[:lon]
  
    if decade != nil && decade.match(/\A(18|19|20)\d0\z/) && decade.to_i < 2050 && decade.to_i >= 1900
    
      if lat != nil && lon != nil && lat.match(/^(\-?\d+(\.\d+)?),*(\-?\d+(\.\d+)?)$/) && lon.match(/^(\-?\d+(\.\d+)?),*(\-?\d+(\.\d+)?)$/)
  
        orderString = "abs(decade - #{decade}), (6378.7*sqrt(POW((0.0174 * (lat - #{lat})),2) + POW((0.0174 * (lon - #{lon}) * COS(#{lat})),2))), distance, coordinates_trust DESC"
        
        @events = Event.joins(:contextIndex => :city).where("Context_Index.event_id IS NOT NULL").order(orderString).limit(5)
    
      else
        @events = Event.joins(:contextIndex).where("Context_Index.event_id IS NOT NULL").order("abs(decade - #{decade})").limit(5)
      end

      render :rabl, :context_events, :format => "json"

    else
      res = { :success => false,
                :info => "Wrong request: you need to specify a decade",
              }.to_json  
      halt 500, {'Content-Type' => 'application/json'}, res
    end

  end

  after do
    ActiveRecord::Base.connection.close
  end
  
end