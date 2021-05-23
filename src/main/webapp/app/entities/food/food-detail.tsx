import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './food.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IFoodDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const FoodDetail = (props: IFoodDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { foodEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="foodDetailsHeading">
          <Translate contentKey="baigkApp.food.detail.title">Food</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{foodEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="baigkApp.food.name">Name</Translate>
            </span>
          </dt>
          <dd>{foodEntity.name}</dd>
          <dt>
            <span id="catory">
              <Translate contentKey="baigkApp.food.catory">Catory</Translate>
            </span>
          </dt>
          <dd>{foodEntity.catory}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="baigkApp.food.description">Description</Translate>
            </span>
          </dt>
          <dd>{foodEntity.description}</dd>
          <dt>
            <span id="calo">
              <Translate contentKey="baigkApp.food.calo">Calo</Translate>
            </span>
          </dt>
          <dd>{foodEntity.calo}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="baigkApp.food.price">Price</Translate>
            </span>
          </dt>
          <dd>{foodEntity.price}</dd>
        </dl>
        <Button tag={Link} to="/food" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/food/${foodEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ food }: IRootState) => ({
  foodEntity: food.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(FoodDetail);
