class ReminiscensAPI < Sinatra::Application
  
  #Media

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
      render :rabl, :media, :format => "json"
    else
      res = { :success => false,
                  :info => "Parameter 'decade' must follow the pattern 'yyyy'; Warning: you need to specify at least one parameter!",
                }.to_json  
      halt 400, {'Content-Type' => 'application/json'}, res
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

        @events=Event.joins(:fuzzyDate, :location).where("decade = ?
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
      res = { :success => false,
                  :info => "Parameter 'decade' must follow the pattern 'yyyy'; Warning: you need to specify at least one parameter!",
                }.to_json  
      halt 400, {'Content-Type' => 'application/json'}, res
    end

  end

  #Works
  get '/works' do
    if params[:decade]!=nil && params[:decade].match(/\A(18|19|20)\d0\z/)

      @mediaMDs=CreativeWork.joins(:fuzzyDate).where("decade = ? ", params[:decade])
    end

    if defined?(@mediaMDs)
      render :rabl, :works, :format => "json"
    else
      res = { :success => false,
                  :info => "Parameter 'decade' must follow the pattern 'yyyy'; Warning: you need to specify at least one parameter!",
                }.to_json  
      halt 400, {'Content-Type' => 'application/json'}, res
    end
  end

  #People
  get "/people" do
    if params[:decade]!=nil && params[:decade].match(/\A(18|19|20)\d0\z/)
      if params[:lat]!=nil && params[:lon]!=nil
        if params[:radius]!=nil
          @radius=params[:radius]
        else
          @radius=0
        end

        @part=FamousPerson.find_by_sql([
#          "SELECT pa.*
#          FROM Participant pa, Person pe
#          WHERE pa.person_id=pe.person_id AND
#          pe.famous = 1 AND pa.life_event_id IN (SELECT life_event_id FROM Life_Event l,
#          Location lo, Fuzzy_Date f WHERE
#          l.location_id=lo.location_id AND
#          l.fuzzy_startdate=f.fuzzy_date_id AND
#          decade = ? AND
#          (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
#          POW((0.0174 * (lon - ?) * COS(?)),2))) <= ? AND
#          type = ? )",
          " SELECT fp.*
            FROM Famous_Person fp, Location l, Fuzzy_Date fd
            WHERE 
              fp.birthplace_id = l.location_id
              AND fp.birthdate_fuzzy_id = fd.fuzzy_date_id
              AND fd.decade = ? 
              AND (6378.7*sqrt(POW((0.0174 * (l.lat - ?)),2) +
                  POW((0.0174 * (l.lon - ?) * COS(?)),2))) 
              <= ? ",
          params[:decade], params[:lat], params[:lon], params[:lat], @radius#, params[:type]
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

        @part=FamousPerson.find_by_sql([
#          "SELECT pe.*
#          FROM Participant pa, Person pe
#          WHERE pa.person_id=pe.person_id AND
#          pe.famous = 1 AND pa.life_event_id IN (SELECT life_event_id FROM Life_Event l,
#          Location lo, Fuzzy_Date f WHERE
#          l.location_id=lo.location_id AND
#          l.fuzzy_startdate=f.fuzzy_date_id AND
#          decade = ? AND
#          (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
#          POW((0.0174 * (lon - ?) * COS(?)),2))) <= ? AND
#          type = ? )",
          " SELECT fp.*
            FROM Famous_Person fp, Location l, Fuzzy_Date fd
            WHERE
              fp.birthplace_id = l.location_id
              AND fp.birthdate_fuzzy_id = fd.fuzzy_date_id
              AND fd.decade = ?
              AND (6378.7*sqrt(
                          POW((0.0174 * (l.lat - ?)),2) +
                          POW((0.0174 * (l.lon - ?) * COS(?)),2)
                          )
                  ) <= ? ",
          params[:decade], @place.lat, @place.lon, @place.lat, @radius#, params[:type]
          ])
      else

        @part=FamousPerson.find_by_sql([
#            "SELECT pe.*
#            FROM Participant pa, Person pe
#            WHERE pa.person_id=pe.person_id AND
#            pe.famous = 1 AND pa.life_event_id IN
#            (SELECT life_event_id FROM Life_Event l,
#            Fuzzy_Date f WHERE l.fuzzy_startdate=f.fuzzy_date_id AND
#            type = ? AND decade = ? )", 
          " SELECT fp.*
            FROM Famous_Person fp, Fuzzy_Date fd
            WHERE
              fp.birthdate_fuzzy_id = fd.fuzzy_date_id
              AND fd.decade = ?",
          params[:decade]
          ])
      end

    elsif params[:lat]!=nil && params[:lon]!=nil
      if params[:radius]!=nil
          @radius=params[:radius]
      else
        @radius=0
      end

      @part=FamousPerson.find_by_sql([
#          "SELECT pe.*
#          FROM Participant pa, Person pe
#          WHERE pa.person_id=pe.person_id AND
#          pe.famous = 1 AND pa.life_event_id IN (SELECT life_event_id FROM Life_Event l,
#          Location lo WHERE
#          l.location_id=lo.location_id AND
#          (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
#          POW((0.0174 * (lon - ?) * COS(?)),2))) <= ? AND
#          type = ? )",
        " SELECT fp.*
          FROM Famous_Person fp, Location l
          WHERE
            fp.birthplace_id = l.location_id
            AND (6378.7*sqrt(
                        POW((0.0174 * (l.lat - ?)),2) +
                        POW((0.0174 * (l.lon - ?) * COS(?)),2)
                        )
                ) <= ? ",

        params[:lat], params[:lon], params[:lat], @radius#, params[:type]
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

      @part=FamousPerson.find_by_sql([
#          "SELECT pe.*
#          FROM Participant pa, Person pe
#          WHERE pa.person_id=pe.person_id AND
#          pe.famous = 1 AND pa.life_event_id IN (SELECT life_event_id FROM Life_Event l,
#          Location lo WHERE
#          l.location_id=lo.location_id AND
#          (6378.7*sqrt(POW((0.0174 * (lat - ?)),2) +
#          POW((0.0174 * (lon - ?) * COS(?)),2))) <= ? AND
#          type = ? )",
        " SELECT fp.*
          FROM Famous_Person fp, Location l
          WHERE
            fp.birthplace_id = l.location_id
            AND (6378.7*sqrt(
                        POW((0.0174 * (l.lat - ?)),2) +
                        POW((0.0174 * (l.lon - ?) * COS(?)),2)
                        )
                ) <= ? ",
          @place.lat, @place.lon, @place.lat, @radius#, params[:type]
          ])
      end    
    if defined?(@part)
      render :rabl, :people, :format => "json"
    else
      res = { :success => false,
        :info => "Parameter 'decade' must follow the pattern 'yyyy'; Warning: you need to specify at least one parameter!",
      }.to_json
      halt 400, {'Content-Type' => 'application/json'}, res
    end
  end

  after do
    ActiveRecord::Base.connection.close
  end
end