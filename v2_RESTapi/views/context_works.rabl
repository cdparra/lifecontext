collection @mediaMDs, :root => "works", :object_root => false

attributes :title, :type, :description, :author, :source, :source_url
attributes :resource_url unless :resource_url.nil?

child :fuzzyDate => :time do |time|
  attributes :exact_date => :datetime unless time.exact_date.nil?
  attributes :accuracy unless time.accuracy.nil?
  attributes :decade unless time.decade.nil?
  attributes :year unless time.year.nil?
end

attributes :locale