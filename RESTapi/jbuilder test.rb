require 'jbuilder'

Jbuilder.encode do |json|

  json.author do
    json.name "Nicola"
    json.email_address "muori@muori.it"
    json.url "www.www.it"
  end

  json.comments "zio"


end