/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lp.reminiscens.crawler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lp.reminiscens.crawler.entities.City;
import lp.reminiscens.crawler.entities.Context_Index;
import lp.reminiscens.crawler.entities.Event;
import lp.reminiscens.crawler.entities.Fuzzy_Date;
import lp.reminiscens.crawler.entities.Life_Event;
import lp.reminiscens.crawler.entities.Location;
import lp.reminiscens.crawler.entities.Media;
import lp.reminiscens.crawler.entities.Media_Metadata;
import lp.reminiscens.crawler.entities.Participant;
import lp.reminiscens.crawler.entities.Person;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class Database {

	private final String DB_USER = "reminiscens";
	private final String DB_NAME = "jdbc:mysql://test.lifeparticipation.org:3306/reminiscens2";
	private final String DB_PASSWORD = "timeline@lp2012";
	private final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static SessionFactory factory;
	public int newEvents = 0;
	public int newLives = 0;
	public int newMedia = 0;
	public int newWorks = 0;

	public Database() {
		try {
			Configuration configuration = new Configuration().configure();

			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
					.applySettings(configuration.getProperties())
					.buildServiceRegistry();
			factory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) {
			System.err.println("Failed to create SessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public boolean isCityGeotagged(String cityName) {
		Session session = factory.openSession();
		Transaction tx = null;
		boolean res = false;
		try {
			tx = session.beginTransaction();
			Query query = session
					.createQuery("SELECT lat FROM City WHERE city_name = :name");
			query.setParameter("name", cityName);
			if (query.uniqueResult() != null) {
				res = true;
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return res;
	}

	public Integer addEvent(Event event) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer eventID = null;
		try {
			tx = session.beginTransaction();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			event.setLast_update(now);
			Query query = session
					.createSQLQuery("SELECT event_id, location_id, fuzzy_startdate, fuzzy_enddate FROM Event WHERE source_url = :url");
			query.setParameter("url", event.getSource_url());
			if (!(query.list().isEmpty())) {
				Object[] o = (Object[]) query.uniqueResult();

				event.setEvent_id((Integer) o[0]);

				if (o[1] != null) {
					event.getLocation().setLocation_id((Integer) o[1]);
				}
				if (o[2] != null) {
					event.getStartDate().setFuzzy_date_id((Integer) o[2]);
				}

				if (o[3] != null) {
					if (event.getEndDate() != null) {
						event.getEndDate().setFuzzy_date_id((Integer) o[3]);
					}
				}

				session.update(event);
			} else {
				session.saveOrUpdate(event);
				newEvents++;
			}
			eventID = event.getEvent_id();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return eventID;
	}

	public Integer addMediaToIndex(Context_Index index) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer contextID = null;
		try {
			tx = session.beginTransaction();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			Query query = session
					.createSQLQuery("SELECT context_index_id FROM Context_Index WHERE media_id = :id");
			query.setParameter("id", index.getMedia().getMedia_id());
			if (!(query.list().isEmpty())) {
				Integer cID = (Integer) query.uniqueResult();

				index.setContext_index_id(cID);

				session.update(index);
			} else {
				session.saveOrUpdate(index);
			}
			contextID = index.getContext_index_id();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return contextID;
	}

	public boolean addMediaToIndex_with_ids(int decade, int year,
			double distance, int media_id, int city_id, int coord_trust) {
		Session session = factory.openSession();
		Transaction tx = null;
		boolean result = false;
		try {
			tx = session.beginTransaction();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			Query query = session
					.createSQLQuery("SELECT context_index_id FROM Context_Index WHERE media_id = :id ");
			query.setParameter("id", media_id);
			if (!(query.list().isEmpty())) {
				Integer cID = (Integer) query.uniqueResult();

				query = session
						.createSQLQuery("UPDATE Context_Index SET decade = :dec, year = :y, distance = :dist, media_id = :m_id, city_id = :c_id, "
								+ "coordinates_trust = :c_tr WHERE context_index_id = :con_id ");
				query.setParameter("dec", decade);
				query.setParameter("y", year);
				query.setParameter("dist", distance);
				query.setParameter("m_id", media_id);
				query.setParameter("c_id", city_id);
				query.setParameter("c_tr", coord_trust);
				query.setParameter("con_id", cID);

				query.executeUpdate();

			} else {
				query = session
						.createSQLQuery("INSERT INTO Context_Index (decade, year, distance, media_id, city_id, coordinates_trust) "
								+ "VALUES (:dec, :y, :dist, :m_id, :c_id, :c_tr) ");
				query.setParameter("dec", decade);
				query.setParameter("y", year);
				query.setParameter("dist", distance);
				query.setParameter("m_id", media_id);
				query.setParameter("c_id", city_id);
				query.setParameter("c_tr", coord_trust);

				query.executeUpdate();
			}
			tx.commit();
			result = true;
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
			result = false;
		} finally {
			session.close();
		}
		return result;
	}

	public Integer addMedia(Media photo) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer photoID = null;
		Media tmp = null;
		try {
			tx = session.beginTransaction();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			photo.setLast_update(now);
			Query query = session
					.createSQLQuery("SELECT media_id, location_id, fuzzy_startdate FROM Media WHERE media_url = :url");
			query.setParameter("url", photo.getMedia_url());
			if (!(query.list().isEmpty())) {
				Object[] o = (Object[]) query.uniqueResult();

				photo.setMedia_id((Integer) o[0]);

				if (o[1] != null) {
					photo.getLocation().setLocation_id((Integer) o[1]);
				}
				if (o[2] != null) {
					photo.getTakenDate().setFuzzy_date_id((Integer) o[2]);
				}

				session.update(photo);
			} else {
				session.saveOrUpdate(photo);
				newMedia++;
			}
			photoID = photo.getMedia_id();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return photoID;
	}

	public Integer addParticipant(Participant part) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer personID = null;
		try {
			tx = session.beginTransaction();
			/*
			 * Timestamp now = new Timestamp(System.currentTimeMillis()); Query
			 * query = session.createQuery(
			 * "SELECT person_id FROM Person WHERE source_url = :url");
			 * query.setParameter("url", part.getPerson().getSource_url()); if
			 * (!(query.list().isEmpty())) { person.setPerson_id((Integer)
			 * (query.list().get(0))); session.update(person); } else {
			 */
			session.saveOrUpdate(part);
			newLives++;
			/* } */
			personID = part.getPerson().getPerson_id();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return personID;
	}

	// DEVO RICORDARMI DI CAMBIARE UNA COLONNA DI TIPO TEXT A VARCHAR, SE NO NON
	// VA
	public Integer addParticipant_test(Participant part) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer personID = null;
		Integer eventID = null;
		try {
			tx = session.beginTransaction();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			Query query = session
					.createQuery("SELECT person_id FROM Person WHERE source_url = :url");
			query.setParameter("url", part.getPerson().getSource_url());
			if (!(query.list().isEmpty())) {
				part.getPerson().setPerson_id((Integer) (query.list().get(0)));
				session.update(part.getPerson());
			} else {
				session.saveOrUpdate(part.getPerson());
				newLives++;
			}
			session.saveOrUpdate(part.getLife_event());
			personID = part.getPerson().getPerson_id();
			eventID = part.getLife_event().getLife_event_id();

			query = session
					.createSQLQuery("INSERT INTO Participant (person_id, life_event_id, focus, status, contributor_id) "
							+ "VALUES ( "
							+ personID
							+ ","
							+ eventID
							+ ","
							+ "1" + "," + "'CONFIRMED'" + "," + "1" + " );");
			query.executeUpdate();

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return personID;
	}

	public Integer addWork(Media_Metadata mediaMD) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer workID = null;
		try {
			tx = session.beginTransaction();
			Query query = session
					.createSQLQuery("SELECT media_metadata_id, fuzzy_releasedate FROM Media_Metadata WHERE source_url = :url");
			query.setParameter("url", mediaMD.getSource_url());
			if (!(query.list().isEmpty())) {
				Object[] o = (Object[]) query.uniqueResult();

				mediaMD.setMedia_metadata_id((Integer) o[0]);

				if (o[1] != null) {
					mediaMD.getReleaseDate().setFuzzy_date_id((Integer) o[1]);
				}

				session.update(mediaMD);
			} else {
				session.saveOrUpdate(mediaMD);
				newWorks++;
			}
			workID = mediaMD.getMedia_metadata_id();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return workID;
	}

	// USATA SOLO PER PRENDERE LE COORDINATE DELLE CITTà ITALIANE
	public Integer addCity(City city) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer cityID = null;
		try {
			tx = session.beginTransaction();
			Query query = session
					.createQuery("SELECT city_id FROM City WHERE city_name = :name");
			query.setParameter("name", city.getCity_name());
			if (!(query.list().isEmpty())) {
				city.setCity_id((Integer) (query.list().get(0)));
				session.update(city);
			} else {
				session.saveOrUpdate(city);
			}
			cityID = city.getCity_id();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return cityID;
	}

	public void addCoords(City city) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer cityID = null;
		try {
			tx = session.beginTransaction();
			System.out.println("aggiorno " + city.getCity_name());
			Query query = session
					.createQuery("UPDATE City SET lat = :lat , lon = :lon WHERE city_name = :name");
			query.setParameter("name", city.getCity_name());
			query.setParameter("lat", city.getLat());
			query.setParameter("lon", city.getLon());
			query.executeUpdate();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public List getCities() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<Event> cities = null;
		try {
			tx = session.beginTransaction();
			cities = session.createQuery("FROM City WHERE region <> 'Trentino-Alto Adige' ").list();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return cities;
	}

	public List getEvents() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<Event> events = null;
		try {
			tx = session.beginTransaction();
			events = session.createQuery("FROM Event").list();
			System.out.println("Inizializzo date e luoghi");
			for (Event ev : events) {
				Hibernate.initialize(ev.getStartDate());
				Hibernate.initialize(ev.getLocation());
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return events;
	}

	public List getMDs() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<Media_Metadata> mds = null;
		try {
			tx = session.beginTransaction();
			mds = session.createQuery("FROM Media_Metadata").list();
			System.out.println("Inizializzo date");
			for (Media_Metadata md : mds) {
				if (md.getReleaseDate() != null) {
					Hibernate.initialize(md.getReleaseDate());
				}
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return mds;
	}

	public List getFamousPeople() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<Person> people = null;
		try {
			tx = session.beginTransaction();
			people = session.createQuery("FROM Person WHERE famous = 1").list();
			System.out.println("Inizializzo date e luoghi");
			for (Person p : people) {
				Hibernate.initialize(p.getBirth().getLife_event()
						.getStartDate());
				Hibernate
						.initialize(p.getBirth().getLife_event().getLocation());
				Hibernate.initialize(p.getDeath().getLife_event()
						.getStartDate());
				Hibernate
						.initialize(p.getDeath().getLife_event().getLocation());
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return people;
	}

	public List getMedia() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<Media> photos = null;
		try {
			tx = session.beginTransaction();
			photos = session.createQuery("FROM Media WHERE is_public = 1 ")
					.list();
			System.out.println("Inizializzo date e luoghi");
			for (Media m : photos) {
				Hibernate.initialize(m.getTakenDate());
				Hibernate.initialize(m.getLocation());
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return photos;
	}

	public Object[] getClosestCity(double lat, double lon) {
		Session session = factory.openSession();
		Transaction tx = null;
		Object[] city = null;
		try {
			tx = session.beginTransaction();
			city = (Object[]) (session
					.createSQLQuery(
							"SELECT city_id, (6378.7*sqrt(POW((0.0174 * (lat - :lat)),2) +"
									+ "POW((0.0174 * (lon - :lon) * COS(:lat)),2))) AS distance FROM City "
									+ "WHERE lat IS NOT null ORDER BY distance ASC;")
					.setDouble("lat", lat).setDouble("lon", lon).setDouble("lat", lat).list().get(0));
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return city;
	}

	public City getCityById(Integer city_id) {
		Session session = factory.openSession();
		Transaction tx = null;
		City city = null;
		try {
			tx = session.beginTransaction();
			city = (City) session.createQuery("FROM City WHERE city_id = :id")
					.setInteger("id", city_id).uniqueResult();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return city;
	}

	public List getLife(Person person) {
		Session session = factory.openSession();
		Transaction tx = null;
		SQLQuery query = null;
		List<Life_Event> life = new ArrayList<Life_Event>();
		try {
			tx = session.beginTransaction();
			query = session
					.createSQLQuery("SELECT  l.* "
							+ "FROM Participant p , Life_Event l , Person pers "
							+ "WHERE pers.person_id = :id AND pers.person_id=p.person_id "
							+ "AND p.life_event_id=l.life_event_id  "
							+ "AND p.status='CONFIRMED';");
			query.setParameter("id", person.getPerson_id());

			if (query == null) {
				return null;
			} else {
				List result = query.list();
				Iterator iterator = result.iterator();
				Life_Event ev = null;
				Location l = null;
				Fuzzy_Date f = null;

				while (iterator.hasNext()) {
					Object[] row = (Object[]) iterator.next();
					ev = new Life_Event();
					ev.setEvent_id((Integer) row[0]);
					System.out.println((Integer) row[0]);
					ev.setHeadline((String) row[1]);
					ev.setText((String) row[2]);
					ev.setType((String) row[3]);
					l = (Location) (session.get(Location.class,
							(Integer) row[10]));
					f = (Fuzzy_Date) (session.get(Fuzzy_Date.class,
							(Integer) row[12]));
					ev.setLocation(l);
					ev.setStartDate(f);
					l.setEvent(ev);
					f.setEvent(ev);
					life.add(ev);
					System.out.println("size " + life.size());
				}
			}

			// tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return life;
	}

	public Person getPersonById(Integer person_id) {
		Session session = factory.openSession();
		Transaction tx = null;
		Person person = null;
		try {
			tx = session.beginTransaction();
			person = (Person) session
					.createQuery("FROM Person WHERE person_id = :id")
					.setInteger("id", person_id).uniqueResult();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return person;
	}

	public List getMediaBySource_Flickr() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<Media> photos = null;
		try {
			tx = session.beginTransaction();
			photos = session.createQuery(
					"FROM Media WHERE is_public = 1 AND source = 'flickr'")
					.list();
			System.out.println("Inizializzo date e luoghi");
			for (Media m : photos) {
				Hibernate.initialize(m.getTakenDate());

				Hibernate.initialize(m.getLocation());
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return photos;
	}
}
